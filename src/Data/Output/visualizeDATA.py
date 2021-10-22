from os import path
from pathlib import Path
import matplotlib.pyplot as plt
import numpy as np
import plotly.graph_objects as go
from plotly.subplots import make_subplots
import pandas as pd
import os

if not os.path.exists("images"):
    os.mkdir("images")


def visPlotly(File:path):
    #angle distance transmittance
    angle = []
    distance = []
    tr = []
    with open(File,"r") as file1:
        for line in file1.readlines():
            x = line.split()
            if(len(x)==3):
                angle.append(float(x[0]))
                distance.append(float(x[1])/1000)
                tr.append(float(x[2])*100)
            else:
                angle.append(0)
                distance.append(float(x[0])/1000)
                tr.append(float(x[1])*100)

    #df = pd.DataFrame(dict(x=angle,y=distance,z=tr))
    s = os.path.basename(File).split("_")
    s_val = int(float(s[2]))
    fig = go.Figure()
    x_val = np.arange(s_val,s_val+len(angle))
    fig.add_trace(go.Scatter(x= x_val, y=angle,name="Magassági szög"))
    fig.add_trace(go.Scatter(x= x_val, y=distance,name="Távolság [km]",yaxis="y2"))
    fig.add_trace(go.Scatter(x= x_val, y=tr,name="Optikai áteresztőképesség [%]",yaxis="y3"))

    # Create axis objects

    fig.update_layout(
        xaxis=dict(domain=[0.1,0.9],title="Eltelt idő [s]"
         )
    ,
    yaxis=dict(
        title="Magassági szög",
        titlefont=dict(
            color="#1f77b4"
        ),
        tickfont=dict(
            color="#1f77b4"
        )
    ),
    yaxis2=dict(
        title="Távolság [km]",
        titlefont=dict(
            color="#FF0000"
        ),
        tickfont=dict(
            color="#FF0000"
        ),
        anchor="free",
        overlaying="y",
        side="left",
        position=0
    ),
    yaxis3=dict(
        title="Optikai áteresztőképesség [%]",
        titlefont=dict(
            color="#9467bd"
        ),
        tickfont=dict(
            color="#9467bd"
        ),
        anchor="x",
        overlaying="y",
        side="right",
    ))
    fig.update_layout(
    width=1000,
    )
    fig.write_image(f"images/{os.path.basename(File)}.jpeg")
    #fig.show()
    #fig.write_html('first_figure.html', auto_open=True)




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
    fig.canvas.set_window_title(File) 
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
    ax[1].legend(handles=[p3])
    
 
    plt.show()




for child in Path('.').iterdir():
    if child.is_file():
        print(f"{child.name}\n")
        visPlotly(child)
