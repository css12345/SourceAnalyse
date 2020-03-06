package io.github.css12345.sourceanalyse.jdtparse.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.exception.DependencyResolveException;

public class TestSpecificDirectoryDependencyManager {
	
	private DependencyManager dependencyManager;
	
	@BeforeEach
	public void setUp() {
		dependencyManager = new SpecificDirectoryDependencyManager();
	}

	@Test
	public void testSuccess() {
		String path = "D:\\Users\\lenovo\\workspace\\SpringMVC_Test";
		Project project = new Project();
		project.setPath(path);
		dependencyManager.configureProjectDependencies(project);
		
		assertEquals(41, project.getPathOfDependencies().size());
	}

	@Test
	public void testEmptyResult() {
		String path = "D:\\Users\\lcs\\workspace\\test";
		Project project = new Project();
		project.setPath(path);
		dependencyManager.configureProjectDependencies(project);
		
		assertEquals(0, project.getPathOfDependencies().size());
	}
	
	@Test
	public void testNonDirectory() {
		String path = "D:\\Users\\lcs\\workspace\\SourceAnalyse\\JDTParse\\src\\test\\java\\pers\\cs\\sourceanalyse\\jdtparse\\TestSpecificDirectoryDependencyManager.java";
		File projectRootFile = new File(path);
		Project project = new Project();
		project.setPath(path);
		
		Exception exception = assertThrows(DependencyResolveException.class, () -> dependencyManager.configureProjectDependencies(project));
		assertEquals(String.format("parameter %s is not a directory", projectRootFile), exception.getMessage());
	}
}
