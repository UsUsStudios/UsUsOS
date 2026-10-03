local function dump_table(indent, t)
	for k, v in pairs(t) do
		if type(v) == "table" and k ~= "_G" then
			print(indent .. k)
			dump_table(indent .. "    ", v)
		else
			print(indent .. k, v)
		end
	end
end

print("GLOBAL DUMP")
dump_table("    ", _G)
print()
print("dump complete")
