package io.github.css12345.sourceanalyse.similarityanalyse.entity;

import java.util.List;
import java.util.Map;

import jep.NDArray;

public class Graph {
	private Object initializationObject;

	private Map<Long, String> nodeLabels;

	public Graph(Map<Long, List<Long>> initializationObject, Map<Long, String> nodeLabels) {
		this.initializationObject = initializationObject;
		this.nodeLabels = nodeLabels;
	}
	
	public Graph(List<List<Long>> initializationObject, Map<Long, String> nodeLabels) {
		this.initializationObject = initializationObject;
		this.nodeLabels = nodeLabels;
	}
	
	public Graph(NDArray<long[]> initializationObject, Map<Long, String> nodeLabels) {
		this.initializationObject = initializationObject;
		this.nodeLabels = nodeLabels;
	}

	public Object getInitializationObject() {
		return initializationObject;
	}

	public Map<Long, String> getNodeLabels() {
		return nodeLabels;
	}
}
