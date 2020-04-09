package io.github.css12345.sourceanalyse.jdtparse;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.entity.ASTNodeInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.utils.ProjectResolver;

@Component
public class Params implements InitializingBean {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Environment environment;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ProjectResolver projectResolver;

	private File methodDeclarationIncludeTypesFile;
	private File specialNodesFile;
	private File gradleTasksFile;
	private String GRADLE_USER_HOME;

	private Set<Project> projects = new HashSet<>();


	public void resolveProjects() {
		Map<String, Project> beansOfProjectMap = applicationContext.getBeansOfType(Project.class);
		if (logger.isInfoEnabled()) {
			logger.info("find beans in application context of project, specific value is {}", beansOfProjectMap);
		}

		String valueOfProjects = environment.getProperty("projects");
		if (valueOfProjects != null) {
			if (logger.isInfoEnabled()) {
				logger.info("find configured projects, specific value is {}", valueOfProjects);
			}

			String[] nameOfProjects = valueOfProjects.split(",");
			for (String nameOfProject : nameOfProjects) {
				Project project = resolveConfiguredInformation(nameOfProject);
				if (logger.isInfoEnabled()) {
					logger.info("project name {} resolves successfully, specific resolve result is {}", nameOfProject,
							project);
				}
				if (project != null)
					beansOfProjectMap.put(nameOfProject, project);
			}
		}

		for (Entry<String, Project> entry : beansOfProjectMap.entrySet()) {
			String name = entry.getKey();
			Project project = entry.getValue();
			if (project.getPath() == null) {
				logger.warn("the name {} of project can not be null, this project will be ignore", name);
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("start to resolve project {}, name is {}", project, name);
				}
				
				Project resolvedProject = projectResolver.resolveProject(project);
				if (resolvedProject != null) {
					projects.add(resolvedProject);

					if (logger.isInfoEnabled()) {
						logger.info("add resolved project {} to projects", resolvedProject);
					}
				}
			}
		}
	}

	/**
	 * resolve configured information from {@link #environment}
	 * 
	 * @return a project object only contains the information configured in
	 *         {@link #environment}, if path is not configured, return null.
	 */
	private Project resolveConfiguredInformation(String nameOfProject) {
		String path = getAndCheckPath(nameOfProject);
		if (path == null)
			return null;

		String pathOfDependenciesLocation = getAndCheckPathOfDependenciesLocation(nameOfProject);
		Set<String> wantedPackageNames = getWantedPackageNames(nameOfProject);

		Project project = new Project();
		project.setPath(path);
		project.setPathOfDependenciesLocation(pathOfDependenciesLocation);
		project.setWantedPackageNames(wantedPackageNames);
		return project;
	}

	private Set<String> getWantedPackageNames(String nameOfProject) {
		String wantedPackageNamesKey = String.format("projects.%s.wantedPackageNames", nameOfProject);
		String wantedPackageNamesValue = environment.getProperty(wantedPackageNamesKey);
		if (wantedPackageNamesValue == null) {
			logger.warn("project {} does not set wantedPackageNames, this config will be ignore", nameOfProject);
			return Collections.emptySet();
		}

		Set<String> set = new HashSet<>();
		String[] wantedPackageNames = wantedPackageNamesValue.split(",");
		for (String packageName : wantedPackageNames)
			set.add(packageName.trim());
		return set;
	}

	private String getAndCheckPathOfDependenciesLocation(String nameOfProject) {
		String pathOfDependenciesLocationKey = String.format("projects.%s.pathOfDependencies.location", nameOfProject);
		String pathOfDependenciesLocation = environment.getProperty(pathOfDependenciesLocationKey);
		if (pathOfDependenciesLocation == null) {
			logger.warn("project {} does not set pathOfDependencies.location, this config will be ignore",
					nameOfProject);
			return null;
		}
		return pathOfDependenciesLocation;
	}

	private String getAndCheckPath(String nameOfProject) {
		String pathKey = String.format("projects.%s.path", nameOfProject);
		String path = environment.getProperty(pathKey);
		if (path == null) {
			logger.warn("project {} does not set path, this project will be ignore", nameOfProject);
			return null;
		}
		return path;
	}

	private void setProperties() throws IOException {
		methodDeclarationIncludeTypesFile = applicationContext.getResource(environment
				.getProperty("methodDeclaration.includedNodeTypes.location", "classpath:includedNodeTypes.txt"))
				.getFile();
		if (logger.isInfoEnabled()) {
			logger.info("set methodDeclarationIncludeTypesFile {}", methodDeclarationIncludeTypesFile);
		}
		gradleTasksFile = applicationContext
				.getResource(environment.getProperty("gradleTasks.location",
						"classpath:gradleTasks.txt"))
				.getFile();
		if (logger.isInfoEnabled()) {
			logger.info("set gradleTasksFile {}", gradleTasksFile);
		}
		specialNodesFile = applicationContext
				.getResource(environment.getProperty("ASTNode.specialNodes.location",
						"classpath:specialNodes.txt"))
				.getFile();
		if (logger.isInfoEnabled()) {
			logger.info("set specialNodesFile {}", specialNodesFile);
		}
		ASTNodeInformation.addAll(FileUtils.readLines(specialNodesFile, "UTF-8"));
		GRADLE_USER_HOME = environment.getProperty("GRADLE_USER_HOME");
		if (logger.isInfoEnabled()) {
			logger.info("set GRADLE_USER_HOME {}", GRADLE_USER_HOME);
		}
	}

	public File getMethodDeclarationIncludeTypesFile() {
		return methodDeclarationIncludeTypesFile;
	}
	
	public File getSpecialNodesFile() {
		return specialNodesFile;
	}

	public File getGradleTasksFile() {
		return gradleTasksFile;
	}

	public String getGRADLE_USER_HOME() {
		return GRADLE_USER_HOME;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		setProperties();
		resolveProjects();
	}
}
