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
            angle.append(float(x[0]))
            distance.append(float(x[1]))
            tr.append(float(x[2]))
    
    fig, ax = plt.subplots()
    fig.subplots_adjust(right=0.75)
    twin1 = ax.twinx()
    twin2 = ax.twinx()
    twin2.spines.right.set_position(("axes", 1.1))
    p1, = ax.plot(angle,'b-',label="angle")
    p2, = twin1.plot(distance,'r-',label="distance")
    p3, = twin2.plot(tr,'g-',label="transmittance")

    ax.yaxis.label.set_color(p1.get_color())
    twin1.yaxis.label.set_color(p2.get_color())
    twin2.yaxis.label.set_color(p3.get_color())
    tkw = dict(size=4, width=1.5)
    ax.tick_params(axis='y', colors=p1.get_color(), **tkw)
    twin1.tick_params(axis='y', colors=p2.get_color(), **tkw)
    twin2.tick_params(axis='y', colors=p3.get_color(), **tkw)
    ax.tick_params(axis='x', **tkw)

    ax.legend(handles=[p1,p2,p3])

    plt.show()


for child in Path('.').iterdir():
    if child.is_file():
        if(child.name.__contains__("Data")):
            print(f"{child.name}\n")
            vis(child)