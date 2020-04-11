package io.github.css12345.sourceanalyse.jdtparse.support;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.exception.CommandExecuteException;
import io.github.css12345.sourceanalyse.jdtparse.exception.DependencyResolveException;
import io.github.css12345.sourceanalyse.jdtparse.utils.CommandExecutor;

/**
 * resolve dependencies for gradle project.
 *
 */
public class GradleDependencyManager implements DependencyManager {

	private static Logger logger = LoggerFactory.getLogger(GradleDependencyManager.class);

	private Params params;

	public GradleDependencyManager(Params params) {
		this.params = params;
		this.GRADLE_USER_HOME = params.getGRADLE_USER_HOME();
	}

	// value from environment
	private String GRADLE_USER_HOME;

	@Override
	public void configureProjectDependencies(Project project) {
		try {
			File projectRootFile = new File(project.getPath());
			File buildGradleFile = new File(projectRootFile, "build.gradle");
			String taskString = "\n\n" + params.getGradleTasks();

			String projectPath = ":";
			configureProject(project, buildGradleFile, taskString, projectPath);
		} catch (IOException e) {
			throw new DependencyResolveException("an IOException " + e + " occur", e);
		}
	}

	private void configureProject(Project project, File buildGradleFile, String taskString,
			String projectPath) throws IOException {
		String projectInformation = getInformationOfProject(buildGradleFile, taskString, projectPath);
		String[] projectInformations = projectInformation.split("\n");
		project.setPath(projectInformations[0]);
		projectInformations[1] = projectInformations[1].substring(projectInformations[1].indexOf(':') + 1).trim();
		projectInformations[2] = projectInformations[2].substring(projectInformations[2].indexOf(':') + 1).trim();
		if (StringUtils.isNotEmpty(projectInformations[1]))
			project.setPathOfDependencies(new ArrayList<>(Arrays.asList(projectInformations[1].split(","))));
		if (StringUtils.isNotEmpty(projectInformations[2])) {
			String[] subprojects = projectInformations[2].split(",");
			for (String subprojectPath : subprojects) {
				Project module = new Project();
				module.setRelativePath(subprojectPath);
				module.setWantedPackageNames(project.getWantedPackageNames());
				project.addModule(module);
				configureProject(module, buildGradleFile, taskString, subprojectPath);
			}
		}
	}

	private String getInformationOfProject(File buildGradleFile, String taskString, String projectPath)
			throws IOException {
		// 1.read the initial content of build.gradle
		// 2.replace taskString with projectPath, write the content append with task to build.gradle
		// 3.execute command gradle -q getInformationOfProject
		// 4.write the initial content to build.gradle
		
		String initialContent = FileUtils.readFileToString(buildGradleFile, "UTF-8");
		if (logger.isInfoEnabled()) {
			logger.info("read the initial content of build.gradle, specific is\n{}", initialContent);
		}

		taskString = String.format(taskString, projectPath);
		FileUtils.writeStringToFile(buildGradleFile, taskString, "UTF-8", true);
		if (logger.isInfoEnabled()) {
			logger.info("append the task to build.gradle, specific is \n{}", taskString);
		}

		try {
			String command = "cd" + (CommandExecutor.isWindows ? " /d " : " ");
			command += String.format("%s & gradle %s-q getInformationOfProject", buildGradleFile.getParent(),
					GRADLE_USER_HOME == null ? "" : "-g " + GRADLE_USER_HOME + " ");

			return CommandExecutor.execute(command);
		} catch (CommandExecuteException e) {
			throw new DependencyResolveException("execute command happen an exception " + e, e);
		} finally {
			FileUtils.write(buildGradleFile, initialContent, "UTF-8");
			if (logger.isInfoEnabled()) {
				logger.info("rewrite the initial content to build.gradle");
			}
		}
	}
}