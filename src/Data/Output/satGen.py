
def makeSats(SatNum: int):
    i = 0
    raanIndex = 0
    maxAngle = 180
    angleDelta= 45
    for raan in range(0,maxAngle,angleDelta):
        raanIndex+=1
        for perige in range(0,maxAngle,angleDelta):
            i+=1
            #print(f"1000|0.0002090|{56.0568+180*(raanIndex%2)}|{perige}|{raan}|18.0| SAT_{i}")
            print(f"satellites LOAD     1000     0.0002090     {56.0568+90*(raanIndex%2)}     {perige}     {raan}     18.0     1 1 1 1")



print("satellites GV_BEGIN")


makeSats(1)


print("satellites GV_END")
