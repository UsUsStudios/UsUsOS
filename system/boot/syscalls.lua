local calls = {}

function calls.exit(pcb, request)
	pcb.state = "dead"
	pcb.exit_code = request.code or -1
end

return calls
