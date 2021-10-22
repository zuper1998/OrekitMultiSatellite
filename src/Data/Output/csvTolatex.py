import pandas as pd 


df =  pd.read_csv("table.csv")
df_sortet = df.sort_values(by='S')
print(df_sortet)
for index,rows in df_sortet.iterrows():
    print(rows['S'])
