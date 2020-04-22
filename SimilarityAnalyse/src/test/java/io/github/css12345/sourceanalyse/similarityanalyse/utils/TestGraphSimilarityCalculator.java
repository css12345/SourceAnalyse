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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.similarityanalyse.entity.Graph;
import io.github.css12345.sourceanalyse.similarityanalyse.support.TestProjectComparator.Config;
import jep.NDArray;

@SpringJUnitConfig(Config.class)
public class TestGraphSimilarityCalculator {
	
	@Autowired
	private GraphSimilarityCalculator graphSimilarityCalculator;
	
	@Test
	public void testCalculate() {
		Map<String, List<String>> initializationObject1 = new TreeMap<>();
		Map<String, String> nodeLabels1 = new LinkedHashMap<>();

		Map<String, List<String>> initializationObject2 = new HashMap<>();
		Map<String, String> nodeLabels2 = new HashMap<>();

		initializationObject1.put("1", new LinkedList<>(Arrays.asList("2", "3")));
		initializationObject1.put("2", new ArrayList<>(Arrays.asList("1")));
		initializationObject1.put("3", new ArrayList<>(Arrays.asList("1")));
		nodeLabels1.put("1", "O");
		nodeLabels1.put("2", "H");
		nodeLabels1.put("3", "H");

		initializationObject2.put("1", new ArrayList<>(Arrays.asList("2", "3", "4")));
		initializationObject2.put("2", new ArrayList<>(Arrays.asList("1")));
		initializationObject2.put("3", new ArrayList<>(Arrays.asList("1")));
		initializationObject2.put("4", new ArrayList<>(Arrays.asList("1")));
		nodeLabels2.put("1", "O");
		nodeLabels2.put("2", "H");
		nodeLabels2.put("3", "H");
		nodeLabels2.put("4", "H");

		Graph graph1 = new Graph(initializationObject1, nodeLabels1);
		Graph graph2 = new Graph(initializationObject2, nodeLabels2);
		System.out.println(graphSimilarityCalculator.calculate(graph1, graph2));

		NDArray<long[]> edges1 = new NDArray<long[]>(new long[] { 0, 1, 1, 1, 0, 0, 1, 0, 0 }, 3, 3);
		nodeLabels1.clear();
		nodeLabels1.put("0", "O");
		nodeLabels1.put("1", "H");
		nodeLabels1.put("2", "H");

		List<List<String>> edges2 = new ArrayList<>();
		edges2.add(new ArrayList<>(Arrays.asList("0", "1", "1", "1")));
		edges2.add(new ArrayList<>(Arrays.asList("1", "0", "0", "0")));
		edges2.add(new ArrayList<>(Arrays.asList("1", "0", "0", "0")));
		edges2.add(new ArrayList<>(Arrays.asList("1", "0", "0", "0")));
		nodeLabels2.clear();
		nodeLabels2.put("0", "O");
		nodeLabels2.put("1", "H");
		nodeLabels2.put("2", "H");
		nodeLabels2.put("3", "H");
		graph1 = new Graph(edges1, nodeLabels1);
		graph2 = new Graph(edges2, nodeLabels2);
		System.out.println(graphSimilarityCalculator.calculate(graph1, graph2));
	}
}
