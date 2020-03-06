package io.github.css12345.sourceanalyse.jdtparse.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.jdtparse.ParamsPropertyConfig;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.support.TestGradleDependencyManager.Config;

@SpringJUnitConfig(Config.class)
public class TestGradleDependencyManager {
	
	@TestConfiguration
	@ComponentScan(basePackageClasses = ParamsPropertyConfig.class, excludeFilters = @Filter(classes = TestConfiguration.class))
	static class Config {
		@Bean
		public DependencyManager dependencyManager(Params params) {
			return new GradleDependencyManager(params);
		}
	}
	
	@Autowired
	private DependencyManager dependencyManager;

	@Test
	public void testSingleModuleDependencies() throws IOException {
		String pathname = "D:\\Users\\lcs\\workspace\\SpringBootTest";
		Project project = new Project();
		project.setPath(pathname);
		dependencyManager.configureProjectDependencies(project);
		assertEquals(0, project.getModules().size());
		List<String> pathOfDependencies = project.getPathOfDependencies();
		assertEquals(96, pathOfDependencies.size());
	}
	
	@Test
	public void testMultiModuleDependencies() {
		String pathname = "D:\\Users\\lcs\\workspace\\gs-multi-module";
		Project project = new Project();
		project.setPath(pathname);
		dependencyManager.configureProjectDependencies(project);
		assertEquals(2, project.getModules().size());
		List<String> pathOfDependencies = project.getPathOfDependencies();
		assertEquals(0, pathOfDependencies.size());
		
		Project application = project.getModules().get(0);
		assertEquals(pathname + "\\application", application.getPath());
		assertEquals(0, application.getModules().size());
		assertEquals(72, application.getPathOfDependencies().size());
		
		Project library = project.getModules().get(1);
		assertEquals(pathname + "\\library", library.getPath());
		assertEquals(0, library.getModules().size());
		assertEquals(46, library.getPathOfDependencies().size());
	}
	
	@Test
	public void testOtherMultiModuleDependencies() {
		String pathname = "D:\\tmp\\groovy";
		Project project = new Project();
		project.setPath(pathname);
		project.setWantedPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("org.gradle"))));
		dependencyManager.configureProjectDependencies(project);
		assertEquals(4, project.getModules().size());
		List<String> pathOfDependencies = project.getPathOfDependencies();
		assertEquals(0, pathOfDependencies.size());
		
		Project api = project.getModules().get(0);
		assertEquals(pathname + "\\api", api.getPath());
		assertEquals(0, api.getModules().size());
		assertEquals(3, api.getPathOfDependencies().size());
		
		Project services = project.getModules().get(1);
		assertEquals(pathname + "\\services", services.getPath());
		assertEquals(1, services.getModules().size());
		assertEquals(2, services.getPathOfDependencies().size());
		
		Project services_personService = project.getModules().get(3);
		assertEquals(pathname + "\\services\\personService", services_personService.getPath());
		assertEquals(0, services_personService.getModules().size());
		assertEquals(4, services_personService.getPathOfDependencies().size());
		
		Project shared = project.getModules().get(2);
		assertEquals(pathname + "\\shared", shared.getPath());
		assertEquals(0, shared.getModules().size());
		assertEquals(2, shared.getPathOfDependencies().size());
		
		assertEquals(services_personService.getPath(), services.getModules().get(0).getPath());
		assertEquals(0, services.getModules().get(0).getModules().size());
		assertEquals(4, services.getModules().get(0).getPathOfDependencies().size());
		
		assertTrue(api.getWantedPackageNames().contains("org.gradle"));
	}

}