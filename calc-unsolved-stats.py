import matplotlib.pyplot as plt
from matplotlib.lines import Line2D
import argparse
import sys
from math import floor

print("Give me logs, I'll tell you which models solved what")

instNames = None

def getInstanceName(line):
    return line.strip().split()[0].split('/')[-1].split('.')[0]


def getTimes(lines):
    global instNames
    names = []
    res = []
    for i,l in enumerate(lines):
        if 'Unsolved' in l:
            res.append(-1)
            names.append(getInstanceName(lines[i-1]))

        if 'Resolution' in l:
            res.append(float(l.split()[2][:-2]))
            names.append(getInstanceName(lines[i-2]))

    if instNames is None:
        instNames = names
    return res

def mklabel(line):
    return 'Solution time with ' + line.split()[-1] + ' (seconds)'


times = []
labels = []

for log in sys.argv[1:]:
    with open(log,'r') as f:
        labels.append(f.readline().strip().split()[-1])
        times.append(getTimes(f.readlines()))


N = len(times[0])
for i,t in enumerate(times):
    if len(t) != N:
        print(labels[i],'is',len(t),'long, but',labels[0],'is',N,'long')
        exit()


def unsolved_stats(labels,times):
    
    L = len(labels)
    N = len(times[0])

    for i in range(L):
        print(f"unsolved by {labels[i]}: {times[i].count(-1)}")

    for mask in range(0,1<<L):
        unsolved = []
        solved = []
        for i in range(L):
            if mask & (1<<i):
                solved.append(labels[i])
            else:
                unsolved.append(labels[i])


        counter = 0
        which = []

        for i in range(N):
            ok = True
            for k in range(L):
                if (times[k][i] == -1) ^ ( (mask & (1<<k)) > 0):
                   pass
                else:
                    ok = False
            if ok:
                counter += 1
                which.append(instNames[i])

        print('-',*unsolved,end=' ')
        print('+',*solved,end=' ')
        print(counter)
        #print(which)


unsolved_stats(labels,times)

