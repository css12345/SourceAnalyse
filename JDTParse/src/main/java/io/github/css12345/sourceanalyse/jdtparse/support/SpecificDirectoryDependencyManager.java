package io.github.css12345.sourceanalyse.jdtparse.support;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.exception.DependencyResolveException;

/**
 * a project may be not use maven or gradle, may be all dependencies in a
 * specific directory.<br>
 * pass a directory, and will recursive find all sub directory, until find a
 * directory which all files are end of .jar<br>
 * if it doesn't have a directory like this, return a empty list.
 */
public class SpecificDirectoryDependencyManager implements DependencyManager {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void configureProjectDependencies(Project project) {
		project.setPathOfDependencies(getDependencies(new File(project.getPath())));
	}
	
	private List<String> getDependencies(File projectRootFile) {
		if (logger.isInfoEnabled()) {
			logger.info("start to find dependencies in directory {}", projectRootFile);
		}
		
		if (!projectRootFile.isDirectory()) {
			throw new DependencyResolveException(String.format("parameter %s is not a directory", projectRootFile));
		}

		List<File> subDirectorys = new ArrayList<>();
		List<String> pathOfSubFiles = new ArrayList<>();
		for (File file : projectRootFile.listFiles()) {
			if (file.isDirectory())
				subDirectorys.add(file);
			else
				pathOfSubFiles.add(file.getAbsolutePath());
		}
		
		if (subDirectorys.isEmpty() && !pathOfSubFiles.isEmpty() && isAllJar(pathOfSubFiles)) {
			if (logger.isInfoEnabled()) {
				logger.info("dependencies finded in directory {}", projectRootFile);
				logger.info("result is \n{}", pathOfSubFiles);
			}
			return pathOfSubFiles;
		}
		else {
			for (File subDirectory : subDirectorys) {
				List<String> result = getDependencies(subDirectory);
				if (!result.isEmpty())
					return result;
			}
			
			if (logger.isInfoEnabled()) {
				logger.info("no dependencies found in directory {}", projectRootFile);
			}
			return Collections.emptyList();
		}
	}

	private boolean isAllJar(List<String> pathOfFiles) {
		for (String path : pathOfFiles) {
			if (!path.endsWith(".jar"))
				return false;
		}
		return true;
	}

}
