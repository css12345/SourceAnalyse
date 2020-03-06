package io.github.css12345.sourceanalyse.jdtparse.support;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.exception.CommandExecuteException;
import io.github.css12345.sourceanalyse.jdtparse.exception.DependencyResolveException;
import io.github.css12345.sourceanalyse.jdtparse.utils.CommandExecutor;

/**
 * resolve dependencies for maven project.
 */
public class MavenDependencyManager implements DependencyManager {
	public static String MAVEN_LOCAL_REPOSITORY_PATH;

	private static Logger logger = LoggerFactory.getLogger(MavenDependencyManager.class);

	static {
		String command = "mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout";
		MAVEN_LOCAL_REPOSITORY_PATH = CommandExecutor.execute(command).trim();
		if (logger.isInfoEnabled()) {
			logger.info("maven local repository path is {}", MAVEN_LOCAL_REPOSITORY_PATH);
		}
	}

	/**
	 * use <a href=
	 * "https://maven.apache.org/plugins/maven-dependency-plugin/list-mojo.html">maven-dependency-plugin
	 * and list goal</a> to parse dependencies in pom file, including transitive
	 * dependencies.
	 */
	@Override
	public void configureProjectDependencies(Project project) {
		try {
			File projectRootFile = new File(project.getPath());

			File pomFile = new File(projectRootFile, "pom.xml");

			setModules(projectRootFile, project);

			File dependenciesOutputFile = new File(pomFile.getParentFile(), "result.txt");
			String command = "cd" + (CommandExecutor.isWindows ? " /d " : " ");
			command += String.format("%s & mvn dependency:list -DoutputFile=%s -DoutputAbsoluteArtifactFilename=true",
					pomFile.getParent(), dependenciesOutputFile.getName());

			CommandExecutor.execute(command);

			List<String> pathOfDependencies = resolveDependenciesFromResultFile(dependenciesOutputFile);
			project.setPathOfDependencies(pathOfDependencies);

			recursiveSetPathOfDependenciesForModules(project);
		} catch (IOException e) {
			throw new DependencyResolveException("an IOException " + e + " occur", e);
		} catch (CommandExecuteException e) {
			throw new DependencyResolveException("execute command happen an exception " + e, e);
		}
	}

	private void recursiveSetPathOfDependenciesForModules(Project project) throws IOException {
		for (Project moduleProject : project.getModules()) {
			moduleProject.setPathOfDependencies(
					resolveDependenciesFromResultFile(new File(moduleProject.getPath(), "result.txt")));
			recursiveSetPathOfDependenciesForModules(moduleProject);
		}
	}

	private List<String> resolveDependenciesFromResultFile(File dependenciesOutputFile) throws IOException {
		List<String> lines = FileUtils.readLines(dependenciesOutputFile, "UTF-8");

		boolean isDeleted = dependenciesOutputFile.delete();

		if (logger.isInfoEnabled()) {
			logger.info("the result file {} {} deleted", dependenciesOutputFile, isDeleted ? "has" : "has not");
		}

		List<String> pathOfDependencies = new ArrayList<>();
		for (String line : lines) {
			line = line.trim();
			String[] parts = line.split(":");
			if (parts.length < 3)
				continue;
			pathOfDependencies.add(parts[parts.length - 1]);
		}
		return pathOfDependencies;
	}

	private void setModules(File projectRootFile, Project project) throws IOException {
		String command = "cd /d %s & mvn help:evaluate -Dexpression=project.modules -q -DforceStdout";
		command = String.format(command, projectRootFile.getAbsolutePath());
		
		String result = CommandExecutor.execute(command);

		String[] lines = result.split("\n");

		for (String line : lines) {
			line = line.trim();
			String prefix = "<string>";
			String suffix = "</string>";
			if (line.startsWith(prefix) && line.endsWith(suffix)) {
				String modulePath = line.substring(line.indexOf(prefix) + prefix.length(), line.lastIndexOf(suffix));
				File moduleFile = new File(projectRootFile, modulePath);
				Project moduleProject = new Project();
				moduleProject.setPath(moduleFile.getCanonicalPath());
				moduleProject.setWantedPackageNames(project.getWantedPackageNames());
				setModules(moduleFile, moduleProject);

				project.addModule(moduleProject);
			}
		}
	}
}
