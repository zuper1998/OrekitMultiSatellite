from os import path
from pathlib import Path
from re import X
import matplotlib.pyplot as plt
import numpy as np
import plotly.graph_objects as go
from plotly.subplots import make_subplots
import pandas as pd
import os

cros = []
retro = []

def generateAgreg(File:path):
    df =  pd.read_csv(File)
    out = 0.0
    cnt = 0
    for index,rows in df.iterrows():
        out = out + float(rows['W'])
        cnt = cnt+1
    return out/cnt


def visu(retro,cros):
    fig = go.Figure()
    x_val = ["Kicsit", "Közepes I.", "Közepes II.", "Nagy"]
    fig.add_trace(go.Scatter(y= x_val, x=retro,name="Retrográd"))
    fig.add_trace(go.Scatter(y= x_val, x=cros,name="Keresztpálya"))


    fig.update_layout(
    yaxis_tickformat  = 'eBit/s'
    ,
    xaxis=dict(
        title="Átlagos másodpercenként átvitt összefonódott kvantumbitek",
        
     ),
         width=1000,
    )

    #fig.show()
    fig.write_image(f"agreg.jpeg",scale=3)

for child in Path('.').iterdir():
    if child.is_dir():
        for child2 in Path("./"+child.name).iterdir(): 
            if child2.is_file() and child2.name.__contains__(".csv"):
                print(f"{child}\n")
                if child.name.__contains__("CROS"):
                    cros.append(generateAgreg(child2))
                else:
                    retro.append(generateAgreg(child2))

print(retro)
print(cros)
visu(retro,cros)