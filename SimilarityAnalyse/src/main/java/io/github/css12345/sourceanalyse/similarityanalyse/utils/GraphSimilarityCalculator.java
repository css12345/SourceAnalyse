package io.github.css12345.sourceanalyse.similarityanalyse.utils;

import java.io.File;

import io.github.css12345.sourceanalyse.similarityanalyse.entity.Graph;
import io.github.css12345.sourceanalyse.similarityanalyse.exception.SimilarityCalculateException;
import jep.Interpreter;
import jep.JepException;
import jep.SharedInterpreter;

public class GraphSimilarityCalculator {
	public static double calculate(Graph graph1, Graph graph2) {
		String currentProjectPath = System.getProperty("user.dir");
		String similarityModulePath = new File(currentProjectPath, "similarity").getAbsolutePath().replace('\\', '/');
		String addSimilarityModulePathCommand = String.format("import sys\nsys.path.append('%s')",
				similarityModulePath);
		try (Interpreter interp = new SharedInterpreter()) {
			interp.exec(addSimilarityModulePathCommand);
			interp.exec("from similarity_calculate import calculate");
			return (double) interp.invoke("calculate", graph1.getInitializationObject(), graph1.getNodeLabels(), graph2.getInitializationObject(), graph2.getNodeLabels());
		} catch (JepException e) {
			throw new SimilarityCalculateException("an error occur when calculate similarity of " + graph1 + " and " + graph2, e);
		}
	}
}
