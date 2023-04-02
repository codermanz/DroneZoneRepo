from gremlin_python import statics
import pandas as pd
statics.load_statics(globals())
from gremlin_python.process.graph_traversal import __
from gremlin_python.process.traversal import Column
from gremlin_python.process.traversal import T

def build_graph(g, scenario_csv):
    # read csv file
    scenario_df = pd.read_csv(scenario_csv, sep=';')

    # create edge/node with properties for each row in dataframe
    for idx, df_row in scenario_df.iterrows():
        df_row.dropna(inplace=True)
        attr_dict = get_attr_dict(df_row)

        # create new node
        if df_row['type'] == 'vertex':
            v1 = g.addV(df_row['label']).property('id_intern', df_row['id_intern']).next()

            # add properties to node
            for key in list(attr_dict.keys()):
                val = attr_dict[key]
                g.V(v1).property(key, val).iterate()

        # create new edge
        elif df_row['type'] == 'edge':

            # get affected nodes
            v1_id = df_row['node_out']
            v2_id = df_row['node_in']
            v1 = g.V().has('id_intern', v1_id).toList()[0]
            v2 = g.V().has('id_intern', v2_id).toList()[0]

            # create edge
            e1 = g.V(v1).addE(df_row['label']).to(v2).next()

            # add properties to edge
            for key in list(attr_dict.keys()):
                val = attr_dict[key]
                g.E(e1.id['@value']['relationId']).property(key, val).iterate()


        # error message when csv got typo or something
        else:
            print('Wrong type of element in the .csv')

    return g


def get_attr_dict(row):
    x = int(row.index.to_list()[-1][1:])
    attr_dict = {}
    i = 1
    while i <= x:
        attr_dict[row['p'+str(i)]] = row['v'+str(i)]
        i += 1
    return attr_dict

