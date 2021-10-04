from os import path
from pathlib import Path
import matplotlib.pyplot as plt
import numpy as np
import plotly.graph_objects as go
from plotly.subplots import make_subplots
import pandas as pd

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
                distance.append(float(x[1]))
                tr.append(float(x[2]))
            else:
                angle.append(0)
                distance.append(float(x[0]))
                tr.append(float(x[1]))

    #df = pd.DataFrame(dict(x=angle,y=distance,z=tr))

    fig = go.Figure()
    fig.add_trace(go.Scatter(x= np.arange(len(angle)), y=angle,name="Angle data"))
    fig.add_trace(go.Scatter(x= np.arange(len(angle)), y=distance,name="Distance data",yaxis="y2"))
    fig.add_trace(go.Scatter(x= np.arange(len(angle)), y=tr,name="Transmittance",yaxis="y3"))

    # Create axis objects
    fig.update_layout(
        xaxis=dict(domain=[0,0.8])
    ,
    yaxis=dict(
        title="Angle",
        titlefont=dict(
            color="#1f77b4"
        ),
        tickfont=dict(
            color="#1f77b4"
        )
    ),
    yaxis2=dict(
        title="Distance",
        titlefont=dict(
            color="#FF0000"
        ),
        tickfont=dict(
            color="#FF0000"
        ),
        anchor="x",
        overlaying="y",
        side="right",
    ),
    yaxis3=dict(
        title="Transmittance",
        titlefont=dict(
            color="#9467bd"
        ),
        tickfont=dict(
            color="#9467bd"
        ),
        anchor="free",
        overlaying="y",
        side="right",
        position=0.85
    ))
    fig.update_layout(
    title_text="multiple y-axes example",
    width=800,
)
    fig.show()
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
        if(child.name.__contains__("Data")):
            print(f"{child.name}\n")
            visPlotly(child)
            break