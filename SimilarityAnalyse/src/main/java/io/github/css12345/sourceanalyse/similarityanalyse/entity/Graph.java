package io.github.css12345.sourceanalyse.similarityanalyse.entity;

import java.util.List;
import java.util.Map;

import jep.NDArray;

public class Graph {
	private Object initializationObject;

	private Map<String, String> nodeLabels;

	public Graph(Map<String, List<String>> initializationObject, Map<String, String> nodeLabels) {
		this.initializationObject = initializationObject;
		this.nodeLabels = nodeLabels;
	}
	
	public Graph(List<List<String>> initializationObject, Map<String, String> nodeLabels) {
		this.initializationObject = initializationObject;
		this.nodeLabels = nodeLabels;
	}
	
	public Graph(NDArray<long[]> initializationObject, Map<String, String> nodeLabels) {
		this.initializationObject = initializationObject;
		this.nodeLabels = nodeLabels;
	}

	public Object getInitializationObject() {
		return initializationObject;
	}

	public Map<String, String> getNodeLabels() {
		return nodeLabels;
	}
}
