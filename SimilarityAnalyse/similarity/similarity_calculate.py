from grakel.graph import Graph
from grakel.graph_kernels import GraphKernel

def calculate(initialization_object1, node_labels1, initialization_object2, node_labels2):
    initialization_object1 = convertPyJMapToDict(initialization_object1, True)
    initialization_object2 = convertPyJMapToDict(initialization_object2, True)
    node_labels1 = convertPyJMapToDict(node_labels1, False)
    node_labels2 = convertPyJMapToDict(node_labels2, False)
    
    graph1 = Graph(initialization_object=initialization_object1, node_labels=node_labels1)
    graph2 = Graph(initialization_object=initialization_object2, node_labels=node_labels2)
    graph_kernel = GraphKernel(kernel="shortest_path", normalize=True)
    graph_kernel.fit_transform([graph1])
    return graph_kernel.transform([graph2])[0][0]

def convertPyJMapToDict(pyJMap, convertValueToList):
    dictory = {}
    
    for entry in pyJMap.entrySet():
        key = entry.getKey()
        value = entry.getValue()
        if (convertValueToList):
            value = convertPyJListToList(value)
        dictory[key] = value
        
    return dictory

def convertPyJListToList(pyJList):
    list = []
    for element in pyJList:
        list.append(element)
    return list