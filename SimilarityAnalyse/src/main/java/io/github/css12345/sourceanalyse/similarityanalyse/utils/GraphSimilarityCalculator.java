package io.github.css12345.sourceanalyse.similarityanalyse.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.Graph;
import io.github.css12345.sourceanalyse.similarityanalyse.exception.SimilarityCalculateException;
import jep.Interpreter;
import jep.JepException;
import jep.SharedInterpreter;

@Component
public class GraphSimilarityCalculator {
	
	@Autowired
	private Params params;
	
	public double calculate(Graph graph1, Graph graph2) {
		String SITE_DATA_DIR = params.getSITE_DATA_DIR();
		String similarityModulePath = new File(SITE_DATA_DIR, "similarity").getAbsolutePath().replace('\\', '/');
		copyFilesIfFilesNotExist(similarityModulePath);
		String addSimilarityModulePathCommand = String.format("import sys\nsys.path.append('%s')",
				similarityModulePath);
		try (Interpreter interp = new SharedInterpreter()) {
			interp.exec(addSimilarityModulePathCommand);
			interp.exec("from similarity_calculate import calculate");
			return (double) interp.invoke("calculate", graph1.getInitializationObject(), graph1.getNodeLabels(),
					graph2.getInitializationObject(), graph2.getNodeLabels());
		} catch (JepException e) {
			throw new SimilarityCalculateException(
					"an error occur when calculate similarity of " + graph1 + " and " + graph2, e);
		}
	}

	private void copyFilesIfFilesNotExist(String directoryPath) {
		File directory = new File(directoryPath);
		if (!directory.exists())
			directory.mkdirs();
		
		List<String> checkedPaths = new ArrayList<>(Arrays.asList("__init__.py", "similarity_calculate.py"));
		for (String checkedPath : checkedPaths) {
			File checkedFile = new File(directory, checkedPath);
			if (!checkedFile.exists()) {
				try {
					checkedFile.createNewFile();
					InputStream sourceInputStream = new ClassPathResource("similarity/" + checkedPath).getInputStream();
					FileUtils.copyToFile(sourceInputStream, checkedFile);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
	}
}
