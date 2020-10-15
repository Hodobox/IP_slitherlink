import matplotlib.pyplot as plt 
import sys

if len(sys.argv) != 3:
    print("Usage: python3",sys.argv[0],"path/log1.txt path/log2.txt")
    exit()

def getTimes(lines):
    res = []
    for l in lines:
        if 'Unsolved' in l:
            res.append(-1)

        if 'Resolution' in l:
            res.append(float(l.split()[2][:-2]))
    return res

with open(sys.argv[1],'r') as A:
    xlabel = A.readline().strip()
    xlim = int(A.readline().strip().split()[-1])
    x = getTimes(A.readlines())

with open(sys.argv[2],'r') as B:
    ylabel = B.readline().strip()
    ylim = int(B.readline().strip().split()[-1])
    y = getTimes(B.readlines())

assert len(x) == len(y)

mx = max(x+y)

for i in range(len(x)):
    if x[i] == -1:
        x[i] = mx + 5
    if y[i] == -1:
        y[i] = mx + 5

plt.xlabel(xlabel)
plt.ylabel(ylabel)
plt.scatter(x,y,2)
plt.show()