import matplotlib.pyplot as plt 
import sys

if len(sys.argv) != 2:
    print("Usage: python3",sys.argv[0],"path/log1.txt")
    exit()

def getInstanceName(line):
    return line.strip().split()[0].split('/')[-1].split('.')[0]

def getTimes(lines):
    res = {}
    for i,l in enumerate(lines):
        if 'Unsolved' in l:
            res[ getInstanceName(lines[i-1]) ] = -1 

        if 'Resolution' in l:
            res[ getInstanceName(lines[i-2]) ] = float(l.split()[2][:-2])
    return res

with open(sys.argv[1],'r') as A:
    model = A.readline().strip()
    timeLimit = int(A.readline().strip().split()[-1])
    times = getTimes(A.readlines())

x = []
y = []

for instance in times:
    try:
        data = {}
        with open('Slitherink/data/analysis/' + instance + ".txt",'r') as stats:
            lines = stats.readlines()
            for l in lines:
                data[l.strip().split()[0]] = ' '.join(l.strip().split()[1:])
    except:
        continue

    y.append(times[instance])

    n = int(data['n'])
    v = int(data['counts'].split()[0]) / ((n-1) * (n-1))
    x.append(v)

#y = [t if t>=0 else max(y)+1 for t in y]

print(len(x),len(y))

ylabel = 'Time to solve by ' + model
xlabel = 'Proportion of no-clues'

plt.xlabel(xlabel)
plt.ylabel(ylabel)
plt.scatter(x,y,2)
plt.show()