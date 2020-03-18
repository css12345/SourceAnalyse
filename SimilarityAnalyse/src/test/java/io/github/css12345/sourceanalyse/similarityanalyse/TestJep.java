package io.github.css12345.sourceanalyse.similarityanalyse;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jep.Interpreter;
import jep.JepException;
import jep.SharedInterpreter;

public class TestJep {

	@Test
	public void test() throws JepException {
		String currentProjectPath = System.getProperty("user.dir");
		String similarityModulePath = new File(currentProjectPath, "similarity").getAbsolutePath().replace('\\', '/');
		String addSimilarityModulePathCommand = String.format("import sys\nsys.path.append('%s')",
				similarityModulePath);
//		SharedInterpreter.setConfig(new JepConfig().setRedirectOutputStreams(true));
		try (Interpreter interp = new SharedInterpreter()) {
			interp.exec(addSimilarityModulePathCommand);
			interp.exec("from similarity_calculate import calculate");

			Map<Integer, List<Integer>> initializationObject1 = new HashMap<>();
			Map<Integer, String> nodeLabels1 = new HashMap<>();

			Map<Integer, List<Integer>> initializationObject2 = new HashMap<>();
			Map<Integer, String> nodeLabels2 = new HashMap<>();

			initializationObject1.put(1, new ArrayList<>(Arrays.asList(2, 3)));
			initializationObject1.put(2, new ArrayList<>(Arrays.asList(1)));
			initializationObject1.put(3, new ArrayList<>(Arrays.asList(1)));
			nodeLabels1.put(1, "O");
			nodeLabels1.put(2, "H");
			nodeLabels1.put(3, "H");

			initializationObject2.put(1, new ArrayList<>(Arrays.asList(2, 3, 4)));
			initializationObject2.put(2, new ArrayList<>(Arrays.asList(1)));
			initializationObject2.put(3, new ArrayList<>(Arrays.asList(1)));
			initializationObject2.put(4, new ArrayList<>(Arrays.asList(1)));
			nodeLabels2.put(1, "O");
			nodeLabels2.put(2, "H");
			nodeLabels2.put(3, "H");
			nodeLabels2.put(4, "H");

			double result = (double) interp.invoke("calculate", initializationObject1, nodeLabels1,
					initializationObject2, nodeLabels2);
			System.out.println(result);
		}
	}
}
