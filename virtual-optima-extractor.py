import sys

if len(sys.argv) != 4:
    print("Usage: python3",sys.argv[0]," $input.txt $output/dir/path/ file-prefix")
    exit(1)

count = 0
lnum = 0
with open(sys.argv[1],"r+",errors='ignore') as data:
    while True:
        try:
            line = data.readline().strip()
        except Exception as e:
            print(lnum,line,e)
            break
        lnum += 1
        if 'end of file' in line:
            break

        if 'Table' not in line:
            continue

        r,c = [int(x) for x in data.readline().strip().split()]
        lnum += 1

        if r != c:
            #print('Skipping',r,c)
            continue

        lnum += r

        board = []
        for _ in range(r):
            board.append([ int(c) if c != '.' else -1 for c in data.readline().strip().split() ])

        fname = sys.argv[2] + sys.argv[3]
        strcount = str(count)
        while len(strcount) < 3:
            strcount = "0" + strcount

        count += 1

        fname += strcount + ".in"

        print('Writing',r+1,'to',fname)

        with open(fname,'w+') as f:
            print(r+1,file=f)
            for row in board:
                print(*row,file=f)
