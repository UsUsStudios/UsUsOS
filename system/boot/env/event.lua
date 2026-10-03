local event = {}

function event.pull(filter, timeout)
	local event_queue = scheduler.running.event_queue

	if timeout and timeout < 0 then
		return nil, "EINVAL", "cannot sleep for a negative amount of time"
	end

	local endtime = chip.getTime() + (timeout or 0)
	while true do
		if endtime <= chip.getTime() and timeout then
			return
		end
		if #event_queue > 0 then
			local possible = table.remove(event_queue, 1)
			if filter == nil or possible[1] == filter then
				return table.unpack(possible)
			end
		end
		coroutine.yield()
	end
end

function event.poll()
	local event_queue = scheduler.running.event_queue

	if #event_queue > 0 then
		return table.unpack(table.remove(event_queue, 1))
	end
end

function event.push(name, ...)
	table.insert(scheduler.running.event_queue, { name, ... })

	return true
end

function event.clear()
	scheduler.running.event_queue = {}
end

return event
