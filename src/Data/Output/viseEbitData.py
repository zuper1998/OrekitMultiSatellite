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

    tr = []
    with open(File,"r") as file1:
        for line in file1.readlines():
            tr.append(float(line))

    #df = pd.DataFrame(dict(x=angle,y=distance,z=tr))

    fig = go.Figure()
    fig.add_trace(go.Scatter(x= np.arange(len(tr)), y=tr,name="Entangled QBITS"))
   
    # Create axis objects
    fig.update_layout(
        xaxis=dict(domain=[0.1,0.9],title="Eltelt idő [s]")
    ,
    yaxis=dict(
        title="Összefonódott kvantumbitek / másorperc",
        titlefont=dict(
            color="#1f77b4"
        ),
        tickfont=dict(
            color="#1f77b4"
        )
    ))
  
    fig.update_layout(
    width=1000,
    )
    fig.write_image(f"images/{os.path.basename(File)}.jpeg")




for child in Path('.').iterdir():
    if child.is_file():
        print(f"{child.name}\n")
        visPlotly(child)
