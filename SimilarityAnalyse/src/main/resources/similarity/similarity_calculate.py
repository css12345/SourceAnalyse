from grakel.graph import Graph
from grakel.graph_kernels import GraphKernel

def calculate(initialization_object1, node_labels1, initialization_object2, node_labels2):
    initialization_object1 = transform(initialization_object1)
    initialization_object2 = transform(initialization_object2)
    node_labels1 = transform(node_labels1)
    node_labels2 = transform(node_labels2)
    
    graph1 = Graph(initialization_object=initialization_object1, node_labels=node_labels1)
    graph2 = Graph(initialization_object=initialization_object2, node_labels=node_labels2)
    graph_kernel = GraphKernel(kernel="shortest_path", normalize=True)
    graph_kernel.fit_transform([graph1])
    return graph_kernel.transform([graph2])[0][0]

def transform(obj):
    if (hasattr(obj, "java_name") == False):
        return obj
    obj_java_name = getattr(obj, "java_name")
    if (obj_java_name == "java.util.HashMap" or obj_java_name == "java.util.LinkedHashMap" or obj_java_name == "java.util.TreeMap"):
        return convertPyJMapToDict(obj)
    if (obj_java_name == "java.util.ArrayList" or obj_java_name == "java.util.LinkedList"):
        return convertPyJListToList(obj)
    return obj

def convertPyJMapToDict(pyJMap):
    dictory = {}
    
    for entry in pyJMap.entrySet():
        key = entry.getKey()
        value = entry.getValue()
        key = transform(key)
        value = transform(value)
        dictory[key] = value
        
    return dictory

def convertPyJListToList(pyJList):
    list = []
    for element in pyJList:
        element = transform(element)
        list.append(element)
    return list