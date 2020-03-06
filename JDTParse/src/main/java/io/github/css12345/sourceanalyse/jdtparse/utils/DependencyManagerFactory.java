package io.github.css12345.sourceanalyse.jdtparse.utils;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.jdtparse.support.DependencyManager;
import io.github.css12345.sourceanalyse.jdtparse.support.GradleDependencyManager;
import io.github.css12345.sourceanalyse.jdtparse.support.MavenDependencyManager;
import io.github.css12345.sourceanalyse.jdtparse.support.SpecificDirectoryDependencyManager;

@Component
public class DependencyManagerFactory {
	@Autowired
	private Params params;
	
	public DependencyManager getDependencyManager(String projectRootPath) {
		if (isMavenProject(projectRootPath))
			return new MavenDependencyManager();
		if (isGradleProject(projectRootPath))
			return new GradleDependencyManager(params);
		return new SpecificDirectoryDependencyManager();
	}

	private static boolean isGradleProject(String projectRootPath) {
		if (new File(projectRootPath, "build.gradle").exists())
			return true;
		return false;
	}

	private static boolean isMavenProject(String projectRootPath) {
		if (new File(projectRootPath, "pom.xml").exists())
			return true;
		return false;
	}
}
