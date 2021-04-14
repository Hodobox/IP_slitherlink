import matplotlib.pyplot as plt
from matplotlib.lines import Line2D
import argparse
import sys
from math import floor

parser = argparse.ArgumentParser()
parser.add_argument('logX',type=str,help='log file whose data go on the x-axis')
parser.add_argument('logY',type=str,help='log file whose data go on the y-axis')
parser.add_argument('-s',type=str,help='file to save image to')
parser.add_argument('-S',action='store_true',help='print statistics')
parser.add_argument('-L',type=int,help='limit of the axis (default: timelimit of log file + 5')
parser.add_argument('-U',action='store_true',help='print unsolved statistics')

args = parser.parse_args()

A = args.logX
B = args.logY

def print_stats(label, stats):
    print(f"{label}---")
    print(f"solved: {len(stats)-stats.count(-1)}/{len(stats)}")

    solved = []
    for t in stats:
        if t > 0:
            solved.append(t)

    if len(solved) == 0:
        return

    total = sum(solved)
    print(f"total runtime: {total}")
    solved.sort()
    print(f"median runtime: {solved[len(solved)//2]}")
    print(f"90th percentile: {solved[floor(len(solved)*0.9)]}")
    print(f"average runtime: {sum(solved)/len(solved)}")


def getTimes(lines):
    res = []
    for l in lines:
        if 'Unsolved' in l:
            res.append(-1)

        if 'Resolution' in l:
            res.append(float(l.split()[2][:-2]))
    return res

def mklabel(line):
    return 'Solution time with ' + line.split()[-1] + ' (seconds)'

with open(sys.argv[1],'r') as A:
    xlabel = mklabel(A.readline().strip())
    xlim = int(A.readline().strip().split()[-1])
    x = getTimes(A.readlines())

with open(sys.argv[2],'r') as B:
    ylabel = mklabel(B.readline().strip())
    ylim = int(B.readline().strip().split()[-1])
    y = getTimes(B.readlines())

try:
    assert len(x) == len(y)
except:
    print('cant compare, as data lengths are',len(x),len(y),'\n. Are you sure both were run on the same dataset?')
    exit(1)

if args.S:
    print_stats(xlabel,x)
    print_stats(ylabel,y)


def unsolved_stats(xlabel,ylabel,x, y):
    print(f"unsolved by {xlabel}: {x.count(-1)}")
    print(f"unsolved by {ylabel}: {y.count(-1)}")

    XSYU, XUYS, XUSU = 0,0,0

    for i in range(len(x)):
        if x[i] == -1 and y[i] == -1:
            XUSU += 1
        elif x[i] == -1:
            XUYS += 1
        elif y[i] == -1:
            XSYU += 1

    print(f"unsolved both: {XUSU}")
    print(f"solved by {xlabel} not by {ylabel}: {XSYU}")
    print(f"solved by {ylabel} not by {xlabel}: {XUYS}")


if args.U:
    unsolved_stats(xlabel,ylabel,x,y)

mx = max(xlim,ylim)

for i in range(len(x)):
    if x[i] == -1:
        x[i] = mx + 3
    if y[i] == -1:
        y[i] = mx + 3

plt.xlabel(xlabel)
plt.ylabel(ylabel)

LIM = args.L 
if LIM is None:
    LIM = mx+5

plt.scatter(x,y,2)
plt.gca().set(xlim=(0,LIM),ylim=(0,LIM))
plt.plot([0,mx],[0,mx],linewidth=0.5,c='r')
plt.plot([0,mx],[mx,mx],'--',linewidth=0.5,c='b')
plt.plot([mx,mx],[0,mx],'--',linewidth=0.5,c='b')

legend_elems =  [
    Line2D([0],[0],color='r',lw=0.5,label='y=x'),
    Line2D([0],[0],linestyle='--',color='b',lw=0.5,label='Time limit')
]

plt.gca().legend(handles=legend_elems, bbox_to_anchor=(0.,1.02,1.,.15),loc='upper left')

if args.s:
    plt.savefig(fname=args.s,dpi=300)

plt.show()

