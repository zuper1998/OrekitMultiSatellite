from os import path
from pathlib import Path
import matplotlib.pyplot as plt
import numpy as np
def vis(File:path):
    #angle distance transmittance
    angle = []
    distance = []
    tr = []
    with open(File,"r") as file1:
        for line in file1.readlines():
            x = line.split()
            if(len(x)==3):
                angle.append(float(x[0]))
                distance.append(float(x[1]))
                tr.append(float(x[2]))
            else:
                angle.append(0)
                distance.append(float(x[0]))
                tr.append(float(x[1]))
    
    

    
    
    fig, ax = plt.subplots(2, figsize=(10,15))
    
    twin1 = ax[0].twinx()

    
    p1, = ax[0].plot(angle,'b-',label="angle")
    p2, = twin1.plot(distance,'r-',label="distance")


    p3, = ax[1].plot(tr,'g-',label="transmittance")

    ax[0].yaxis.label.set_color(p1.get_color())
    twin1.yaxis.label.set_color(p2.get_color())
    ax[1].yaxis.label.set_color(p3.get_color())
 
    tkw = dict(size=3, width=1.5)
    ax[0].tick_params(axis='y', colors=p1.get_color(), **tkw)
    twin1.tick_params(axis='y', colors=p2.get_color(), **tkw)

    ax[0].tick_params(axis='x',**tkw)
    ax[0].legend(handles=[p1,p2]) 
    
    
    ax[1].tick_params(axis='y', colors=p3.get_color())

    
 
    plt.show()


for child in Path('.').iterdir():
    if child.is_file():
        if(child.name.__contains__("Data")):
            print(f"{child.name}\n")
            vis(child)