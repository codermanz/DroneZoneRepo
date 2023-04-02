import json
from xml.etree.ElementTree import Element, SubElement, Comment, tostring
from xml.etree import ElementTree as ET
from xml.dom import minidom


def create_graphson(g):
    graph_dict = {"vertices": [], "edges": []}
    all_vertices = g.V().toList()
    all_edges = g.E().toList()

    for vertex in all_vertices:
        vertex_dict = {"id": vertex.id, "type": "vertex", "Label":vertex.label}
        value_map = g.V(vertex).valueMap().toList()[0]
        for key in list(value_map.keys()):
            vertex_dict[key] = value_map[key][0]
        graph_dict["vertices"].append(vertex_dict)

    for edge in all_edges:
        edge_dict = {"id": edge.id['@value']['relationId'], "type": "edge", "outV": edge.outV.id, "inV": edge.inV.id, "Label":edge.label}
        value_map = g.E(edge.id['@value']['relationId']).valueMap().toList()[0]
        for key in list(value_map.keys()):
            edge_dict[key] = value_map[key]
        graph_dict["edges"].append(edge_dict)

    return graph_dict


def graphson_to_graphml(data):

    # Create the root element for the GraphML file
    graphml = Element("graphml", xmlns="http://graphml.graphdrawing.org/xmlns")
    python_to_graphml = {
        'str': "string",
        'float': "double",
        'int': "int",
        'bool': "boolean"
    }

    # Add the key elements to the GraphML file
    node_key_list = ["id", "type"]
    for i in range(0, len(data['vertices'])):
        for key, value in data["vertices"][i].items():
            if key not in node_key_list:
                key_element = SubElement(graphml, "key", **{
                    "id": key,
                    "for": "node",
                    "attr.name": key,
                    "attr.type": python_to_graphml[type(value).__name__]
                })
                node_key_list.append(key)

    edge_key_list = ["id", "type", "outV", "inV"]
    for i in range(0, len(data['edges'])):
        for key, value in data["edges"][i].items():
            if key not in edge_key_list:
                key_element = SubElement(graphml, "key", **{
                    "id": key,
                    "for": "edge",
                    "attr.name": key,
                    "attr.type": python_to_graphml[type(value).__name__]
                })
                edge_key_list.append(key)

# Add the graph element to the GraphML file
    graph = SubElement(graphml, "graph", id="G", edgedefault="undirected")

    # Add the vertices to the GraphML file
    for vertex in data["vertices"]:
        vertex_element = SubElement(graph, "node", id=str(vertex["id"]))
        for key, value in vertex.items():
            if key not in ["id", "type"]:
                data_element = SubElement(vertex_element, "data", key=key)
                data_element.text = str(value)

    # Add the edges to the GraphML file
    for edge in data["edges"]:
        edge_element = SubElement(graph, "edge", id=str(edge["id"]), source=str(edge["outV"]), target=str(edge["inV"]))
        for key, value in edge.items():
            if key not in ["id", "type", "outV", "inV"]:
                data_element = SubElement(edge_element, "data", key=key)
                data_element.text = str(value)

    xml_string = minidom.parseString(ET.tostring(graphml)).toprettyxml(indent="  ")
    return xml_string


def g2graphml(g):
    graphson = create_graphson(g)
    return graphson_to_graphml(graphson)

