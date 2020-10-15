import os

files = os.listdir(os.getcwd() + "/manual/")

for fname in files:
    with open("manual/" + fname,'r+') as f:
        try:
            n = int(f.readline().strip())
            for _ in range(n-1):
                nums = f.readline().strip().split()
                nums = [int(x) for x in nums]
                for i in range(n-1):
                    assert nums[i] >= -1 and nums[i] <= 4
        except Exception as e:
            print(fname,'is bad because',e)
