_G.scheduler = {}

local wrap_process = include("errors.lua")()

local handle_syscall = nil

scheduler.pid_counter = 0
scheduler.processes = {}
scheduler.time_period = 0.05 -- in seconds
scheduler.cpu_load = 0 -- in a percentage
scheduler.loads = {}
scheduler.ticks = 0

local ready_queue = {} -- the list of pids that should be run next tick

function scheduler.queue(pcb)
	pcb.state = "ready"
	table.insert(ready_queue, pcb.pid)
end

-- create a new process running the function fn with an optional parent pid and args
function scheduler.new_process(fn, parent_pid, args)
	if fn == nil then
		error("cannot start process with function nil")
	end

	scheduler.pid_counter = scheduler.pid_counter + 1
	local pcb = {
		pid = scheduler.pid_counter,
		ppid = parent_pid,
		state = "ready", -- ready | running | zombie | dead
		exit_code = nil,
		children = {},
		to_return = nil, -- return to the coroutine on next resume
		error = nil, -- error message to return to coroutine on next resume
		yields = 0, -- how many yields have been processed by the scheduler
		utime = 0, -- how many seconds has the CPU spent running this process's code
		stime = 0, -- how many seconds has the CPU spent running this process's syscalls
	}
	pcb.co = coroutine.create(function()
		wrap_process(fn, pcb, table.unpack(args or {}))
	end)
	debug.sethook(pcb.co, function()
		if coroutine.isyieldable() then
			coroutine.yield()
		end
	end, "", 100000)

	scheduler.processes[pcb.pid] = pcb
	if parent_pid and scheduler.processes[parent_pid] then
		table.insert(scheduler.processes[parent_pid].children, pcb.pid)
	end
	scheduler.queue(pcb)

	return pcb
end

-- sends some messages when a process dies
function scheduler.dead(pcb, msg, req)
	print("Process with PID " .. pcb.pid .. " ended with exit code " .. pcb.exit_code)
	if type(req) ~= "table" and req then
		print("    error of exit: " .. msg .. req)
	else
		print("    " .. msg)
	end
end

local gettime = chip.getTime
function scheduler.tick()
	scheduler.ticks = scheduler.ticks + 1

	local queue = ready_queue
	ready_queue = {}

	for _, pid in ipairs(queue) do
		local pcb = scheduler.processes[pid]
		if pcb and pcb.state == "ready" then
			local start = gettime()
			pcb.state = "running"
			local ok, req = coroutine.resume(pcb.co, pcb.to_return, pcb.error)
			pcb.error = nil
			pcb.to_return = nil
			local utime = gettime() - start

			if coroutine.status(pcb.co) == "dead" then
				pcb.state = "zombie"
				pcb.exit_code = pcb.exit_code or 0

				scheduler.dead(pcb, "coroutine found dead, ", req)
			elseif not ok then
				-- uncaught error
				pcb.state = "zombie"
				pcb.exit_code = -1

				scheduler.dead(pcb, "uncaught error, ", req)
			else
				local syscall_ok, err = xpcall(handle_syscall, debug.traceback, pcb, req)
				if not syscall_ok and err then
					err = "syscall error: " .. err
					pcb.error = err
				end
				scheduler.queue(pcb)
			end

			local stime = gettime() - start - utime

			pcb.utime = pcb.utime + utime
			pcb.stime = pcb.stime + stime

			scheduler.loads[pid .. "-utime"] = utime / scheduler.time_period * 100
			scheduler.loads[pid .. "-stime"] = stime / scheduler.time_period * 100
		end
	end
end
