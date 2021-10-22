import graphviz
import pandas as pd 


df = pd.read_csv("QSAT_CROS_MID.txt/table.csv")
g = graphviz.Digraph('G', filename='process1.gv', engine='dot')
g.attr(rankdir='LR', size='50')
for index,rows in df.iterrows():
    s = "{0:.2f}".format(float(rows['W']))
    if(rows['S'] == "Budapest"):
        g.edge(rows['S'],rows['T'],label=f"{s} eBits/s")

g.view()