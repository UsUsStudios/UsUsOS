_G.screen.set()
_G.ZEOS_VERSION = "v0.0.1"

function _G.include(path, env)
	local handle = files.open("system:/boot/" .. path)
	local data = handle.read("a")
	handle.close()
	local f, err = load(data, "system:/boot/" .. path, nil, env or _G)
	if err then
		error(err)
	end
	return f
end

function _G.panic(cause, msg)
	print()
	print("####################################################")
	print("################### KERNEL PANIC ###################")
	print("####################################################")
	print("Cause: " .. cause)
	print(msg)
	chip.shutdown()
end

include("scheduler.lua")()

scheduler.new_process(function()
	print("hello 1")
	coroutine.yield({ call = "exit" })
	print("hello 2")
end)

local gettime = chip.getTime
local loads = scheduler.loads
local pid1 = scheduler.processes[1]

while true do
	local last_time = gettime()

	scheduler.tick()
	local start = gettime()
	local ticking_time = gettime() - last_time
	if pid1.state ~= "ready" then
		panic(
			"PID 1 is dead",
			"exit code: "
				.. tostring(pid1.exit_code)
				.. "\nerror code: "
				.. tostring(pid1.error_code)
				.. "\nerror message: "
				.. tostring(pid1.error_msg)
		)
	end
	scheduler.cpu_load = ticking_time / scheduler.time_period * 100

	-- wait until the next tick is scheduled
	while last_time + scheduler.time_period > gettime() do
		for _ = 0, 100 do
			coroutine.yield()
		end
	end
	loads.idle = (gettime() - start) / scheduler.time_period * 100
end
