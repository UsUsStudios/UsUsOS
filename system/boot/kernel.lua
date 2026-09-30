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

include("scheduler.lua")()

local gettime = chip.getUnixTime
local loads = scheduler.loads
local last_time
local start

while true do
	last_time = gettime()

	scheduler.tick()
	start = gettime()
	local ticking_time = gettime() - last_time
	scheduler.cpu_load = ticking_time / scheduler.time_period * 100

	-- wait until the next tick is scheduled
	while last_time + scheduler.time_period > gettime() do
		for _ = 0, 100 do
			coroutine.yield()
		end
	end
	loads.idle = gettime() - start
end
