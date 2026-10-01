_G.screen.set()
_G.USUSOS_VERSION = "v0.0.1"

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

local i = 1

include("scheduler.lua")()

scheduler.new_process(function()
	while true do
		i = i * 325 ^ 0.326825
	end
end)

scheduler.new_process(function()
	while true do
		i = i / 43 ^ 4 + 5
	end
end)

local gettime = chip.getTime
local loads = scheduler.loads

while true do
	local last_time = gettime()

	scheduler.tick()
	local start = gettime()
	local ticking_time = gettime() - last_time
	scheduler.cpu_load = ticking_time / scheduler.time_period * 100

	-- wait until the next tick is scheduled
	while last_time + scheduler.time_period > gettime() do
		for _ = 0, 100 do
			coroutine.yield()
		end
	end
	loads.idle = (gettime() - start) / scheduler.time_period * 100
end
