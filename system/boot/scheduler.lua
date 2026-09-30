_G.scheduler = {}

scheduler.pid_counter = 1
scheduler.processes = {}
scheduler.time_period = 0.05 -- in seconds
scheduler.cpu_load = 0 -- in a percentage
scheduler.loads = {}

local ready_queue = {}

local function set_preemption_ook(pcb, co)
	debug.sethook(co, function()
		pcb.utime = pcb.utime + 1123 -- utime: how many instructions have been run in this process coroutine
		scheduler.cputime = scheduler.cputime + 1123 + 15 -- the amount of instructions in this debug hook
		coroutine.yield()
	end, "", 1123) -- it's not a round number so that the instruction counts are less round
end

function scheduler.tick()
	-- simulate work
	for _ = 0, 130000 do
		scheduler.pid_counter = scheduler.pid_counter + 1
	end
end
