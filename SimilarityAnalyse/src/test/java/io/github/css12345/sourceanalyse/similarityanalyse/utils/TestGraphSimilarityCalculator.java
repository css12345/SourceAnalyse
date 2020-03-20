package io.github.css12345.sourceanalyse.similarityanalyse.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import io.github.css12345.sourceanalyse.similarityanalyse.entity.Graph;
import jep.NDArray;

public class TestGraphSimilarityCalculator {
	@Test
	public void testCalculate() {
		Map<Long, List<Long>> initializationObject1 = new TreeMap<>();
		Map<Long, String> nodeLabels1 = new LinkedHashMap<>();

		Map<Long, List<Long>> initializationObject2 = new HashMap<>();
		Map<Long, String> nodeLabels2 = new HashMap<>();

		initializationObject1.put(1L, new LinkedList<>(Arrays.asList(2L, 3L)));
		initializationObject1.put(2L, new ArrayList<>(Arrays.asList(1L)));
		initializationObject1.put(3L, new ArrayList<>(Arrays.asList(1L)));
		nodeLabels1.put(1L, "O");
		nodeLabels1.put(2L, "H");
		nodeLabels1.put(3L, "H");

		initializationObject2.put(1L, new ArrayList<>(Arrays.asList(2L, 3L, 4L)));
		initializationObject2.put(2L, new ArrayList<>(Arrays.asList(1L)));
		initializationObject2.put(3L, new ArrayList<>(Arrays.asList(1L)));
		initializationObject2.put(4L, new ArrayList<>(Arrays.asList(1L)));
		nodeLabels2.put(1L, "O");
		nodeLabels2.put(2L, "H");
		nodeLabels2.put(3L, "H");
		nodeLabels2.put(4L, "H");
		
		Graph graph1 = new Graph(initializationObject1, nodeLabels1);
		Graph graph2 = new Graph(initializationObject2, nodeLabels2);
		System.out.println(GraphSimilarityCalculator.calculate(graph1, graph2));
		
		NDArray<long[]> edges1 = new NDArray<long[]>(new long[] {0,1,1,1,0,0,1,0,0}, 3,3);
		nodeLabels1.clear();
		nodeLabels1.put(0L, "O");
		nodeLabels1.put(1L, "H");
		nodeLabels1.put(2L, "H");
		
		List<List<Long>> edges2 = new ArrayList<>();
		edges2.add(new ArrayList<>(Arrays.asList(0L,1L,1L,1L)));
		edges2.add(new ArrayList<>(Arrays.asList(1L,0L,0L,0L)));
		edges2.add(new ArrayList<>(Arrays.asList(1L,0L,0L,0L)));
		edges2.add(new ArrayList<>(Arrays.asList(1L,0L,0L,0L)));
		nodeLabels2.clear();
		nodeLabels2.put(0L, "O");
		nodeLabels2.put(1L, "H");
		nodeLabels2.put(2L, "H");
		nodeLabels2.put(3L, "H");
		graph1 = new Graph(edges1, nodeLabels1);
		graph2 = new Graph(edges2, nodeLabels2);
		System.out.println(GraphSimilarityCalculator.calculate(graph1, graph2));
	}
}
