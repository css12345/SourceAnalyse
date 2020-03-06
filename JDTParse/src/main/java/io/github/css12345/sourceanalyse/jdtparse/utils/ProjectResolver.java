package io.github.css12345.sourceanalyse.jdtparse.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.exception.DependencyResolveException;
import io.github.css12345.sourceanalyse.jdtparse.exception.ProjectResolveException;
import io.github.css12345.sourceanalyse.jdtparse.support.DependencyManager;

@Component
public class ProjectResolver {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * this method do these things<br>
	 * 1.the {@link Project#path} and {@link Project#pathOfDependenciesLocation}(if
	 * not null) starts with "classpath" or "file", it will replace it with absolute
	 * path.<br>
	 * if the file does not exist, return null.<br>
	 * 2.if {@link Project#pathOfDependenciesLocation} is not null, read file
	 * content for pathOfDependencies;else try to use different
	 * {@linkplain io.github.css12345.sourceanalyse.jdtparse.support.DependencyManager#configureProjectDependencies(File)
	 * DependencyManager.getPathOfDependencies(File)} implement to get the
	 * pathOfDependencies.<br>
	 * first will check whether has a pom file or build.gradle file to choose
	 * {@linkplain io.github.css12345.sourceanalyse.jdtparse.support.MavenDependencyManager
	 * MavenDependencyManager} or
	 * {@linkplain io.github.css12345.sourceanalyse.jdtparse.support.GradleDependencyManager
	 * GradleDependencyManager}, if it doesn't have this file,
	 * {@linkplain io.github.css12345.sourceanalyse.jdtparse.support.SpecificDirectoryDependencyManager
	 * SpecificDirectoryDependencyManager} will be used and the project root file as
	 * parameter.
	 */
	public Project resolveProject(Project resolvedProject) {
		try {
			String absolutePath = getAbsolutePath(resolvedProject.getPath());
			if (absolutePath == null)
				return null;
			resolvedProject.setPath(absolutePath);
			if (logger.isDebugEnabled()) {
				logger.debug("set path {} of project", absolutePath);
			}

			String pathOfDependenciesLocation = resolvedProject.getPathOfDependenciesLocation();
			if (pathOfDependenciesLocation != null) {
				String pathOfDependenciesLocationAbsolutePath = getAbsolutePath(pathOfDependenciesLocation);
				if (pathOfDependenciesLocationAbsolutePath == null)
					return null;
				resolvedProject.setPathOfDependenciesLocation(pathOfDependenciesLocationAbsolutePath);
				if (logger.isDebugEnabled()) {
					logger.debug("set pathOfDependenciesLocation {} of project", pathOfDependenciesLocationAbsolutePath);
				}

				if (ObjectUtils.isEmpty(resolvedProject.getPathOfDependencies())) {
					File pathOfDependenciesLocationFile = new File(pathOfDependenciesLocationAbsolutePath);
					List<String> pathOfDependencies = readAndCheckDependencies(pathOfDependenciesLocationFile);
					resolvedProject.setPathOfDependencies(pathOfDependencies);
					
					if (logger.isDebugEnabled()) {
						logger.debug("set pathOfDependencies {} of project", pathOfDependencies);
					}
				}

			} else { 
				if (ObjectUtils.isEmpty(resolvedProject.getPathOfDependencies())) {
					DependencyManagerFactory dependencyManagerFactory = applicationContext.getBean(DependencyManagerFactory.class);
					DependencyManager dependencyManager = dependencyManagerFactory.getDependencyManager(absolutePath);
					dependencyManager.configureProjectDependencies(resolvedProject);
				}
			}
		} catch (Exception e) {
			throw new ProjectResolveException("an exception occur when resolve project, specific is\n" + e, e);
		}

		return resolvedProject;
	}

	private String getAbsolutePath(String pathWithClasspathOrFilePrefix) throws IOException {
		Resource resource = applicationContext.getResource(pathWithClasspathOrFilePrefix);
		File file = resource.getFile();
		if (!file.exists()) {
			logger.warn("file with path {} does not exist, this project will be ignore.",
					pathWithClasspathOrFilePrefix);
			return null;
		}

		return file.getAbsolutePath();
	}

	private List<String> readAndCheckDependencies(File pathOfDependenciesLocationFile) throws IOException {
		List<String> pathOfDependencies = FileUtils.readLines(pathOfDependenciesLocationFile, "UTF-8");
		for (String pathOfDependency : pathOfDependencies) {
			if (!pathOfDependency.endsWith(".jar")) {
				throw new DependencyResolveException(
						String.format("path of dependency %s does not end with .jar", pathOfDependency));
			}

			if (!new File(pathOfDependency).exists()) {
				throw new DependencyResolveException(
						String.format("path of dependency %s does not exist file", pathOfDependency));
			}
		}
		return pathOfDependencies;
	}
}
