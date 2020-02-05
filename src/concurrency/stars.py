import sys

string1 = sys.argv[1]
string2 = sys.argv[2]
string3 = sys.argv[3]

most = max(len(string3), max(len(string2), len(string1)))

for i in range(0, most + 2):
	print("*", end="")

print()

for i in range(0, 3):
	print("*", end="")
	print(sys.argv[i + 1], end="")
	for j in range(0, most - len(sys.argv[i + 1])):
		print(" ", end="")
	print("*")

for i in range(0, most + 2):
	print("*", end="")