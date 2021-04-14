import matplotlib.pyplot as plt 
from matplotlib.lines import Line2D
import argparse
from math import floor

supported = ['n','counts','length']

parser = argparse.ArgumentParser()
parser.add_argument('log',type=str)
parser.add_argument('varname',choices=supported)
parser.add_argument('-n',type=int)
parser.add_argument('-m',action='store_true')
parser.add_argument('-uq',action='store_true')
parser.add_argument('-pt',choices=['scatter','box'],default='scatter')
parser.add_argument('-L',type=int,help='axis limit')
parser.add_argument('-s',type=str)
parser.add_argument('-S',action='store_true')
parser.add_argument('-a',type=float)

args = parser.parse_args()

varname = args.varname
restrn = args.n

def getInstanceName(line):
    return line.strip().split()[0].split('/')[-1].split('.')[0]
 
def getTimes(lines):
    res = {}
    for i,l in enumerate(lines):
        if 'Unsolved' in l:
            res[ getInstanceName(lines[i-1]) ] = timeLimit+1

        if 'Resolution' in l:
            res[ getInstanceName(lines[i-2]) ] = float(l.split()[2][:-2])

    return res

with open(args.log,'r') as A:
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

    data['n'] = int(data['n'])
    data['counts'] = int(data['counts'].split()[0]) / ((data['n']-1) * (data['n']-1))

    if 'length' in data:
        data['length'] = int(data['length'])

    if restrn is not None:
        if data['n'] != restrn:
            continue

    if varname in data:

        y.append(times[instance])    
        x.append(data[varname])


varmap = {
    'n' : 'Size of puzzle (n)',
    'length' : 'Length of solution (in edges)',
    'counts' : 'Proportion of clues present'

}

ylabel = 'Time to solve by ' + model.split()[-1] +  ' (s)'
xlabel = varmap[varname]

plt.xlabel(xlabel)
plt.ylabel(ylabel)

legend_elems = []

if args.pt == 'scatter':
    a = 1 if args.a is None else args.a
    plt.scatter(x,y,2,alpha=a)
else:
    xs = {}
    for i in range(len(y)):
        if x[i] not in xs:
            xs[x[i]] = []

        xs[ x[i] ].append(y[i])

    cols = []
    positions = []
    for k in xs:
        positions.append(k)

    positions.sort()
    for i in range(len(positions)):
        cols.append(xs[positions[i]])

    if args.L:
        plt.ylim((0,args.L))

    if args.S:
        for i in range(len(cols)):
            under_timelimit = []
            for v in cols[i]:
                if v <= timeLimit:
                    under_timelimit.append(v)

            under_timelimit.sort()
            print(f"{positions[i]}: mean={sum(under_timelimit)/len(under_timelimit):.3f}, UQ={under_timelimit[floor(len(under_timelimit)*0.75)]}")


    plt.boxplot(x=cols,positions=positions)


# plot median
if args.m:
    xs = {}
    for i in range(len(y)):
        if x[i] not in xs:
            xs[x[i]] = []

        xs[ x[i] ].append(y[i])

    cols = []
    positions = []
    for k in xs:
        positions.append(k)

    positions.sort()
    for i in range(len(positions)):
        cols.append(xs[positions[i]])

    medians = []

    for i in range(len(cols)):
        cols[i].sort()
        M = int((len(cols[i])*0.5)//1)
        medians.append(cols[i][M])

    plt.plot(positions,medians,c='g',linestyle='--')
    legend_elems.append(Line2D([0],[0],color='g',lw=0.5,linestyle='--',label='median'))



# plot upper quartile
if args.uq:
    xs = {}
    for i in range(len(y)):
        if x[i] not in xs:
            xs[x[i]] = []

        xs[ x[i] ].append(y[i])

    cols = []
    positions = []
    for k in xs:
        positions.append(k)

    positions.sort()
    for i in range(len(positions)):
        cols.append(xs[positions[i]])

    uqs = []

    for i in range(len(cols)):
        cols[i].sort()
        UQ = int((len(cols[i])*0.75)//1)
        uqs.append(cols[i][UQ])

    plt.plot(positions,uqs,c='r',linestyle='--')
    legend_elems.append(Line2D([0],[0],linestyle='--',color='r',lw=0.5,label='upper quartile'))

if legend_elems:
    plt.gca().legend(handles=legend_elems, bbox_to_anchor=(0.,1.02,1.,.15),loc='upper left')


if args.s:
    plt.savefig(fname=args.s,dpi=300)

plt.show()