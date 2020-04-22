package io.github.css12345.sourceanalyse.similarityanalyse.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.utils.ProjectUtils;
import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;
import io.github.css12345.sourceanalyse.persistence.repository.FileInformationRepository;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.ProjectCompare;

@Component
public class ProjectComparator {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FileComparator fileComparator;

	@Autowired
	private FileInformationRepository fileInformationRepository;

	/**
	 * this method aim to find all {@link FileCompare} and set it for argument
	 * projectCompare
	 * 
	 * @param projectCompare must set the compared projects, means
	 *                       {@link ProjectCompare#project1} and
	 *                       {@link ProjectCompare#project2} must not null
	 */
	public void compare(ProjectCompare projectCompare) {
		List<String> filePaths = new ArrayList<>();
		filePaths.addAll(ProjectUtils.findSuffixLikeJavaFiles(projectCompare.getProject1()).stream()
				.map(file -> file.getAbsolutePath()).collect(Collectors.toList()));
		filePaths.addAll(ProjectUtils.findSuffixLikeJavaFiles(projectCompare.getProject2()).stream()
				.map(file -> file.getAbsolutePath()).collect(Collectors.toList()));
		compare(projectCompare, filePaths);
	}

	public String getAnotherPath(Project project1, Project project2, String path1) {
		Project closestProjectOfProject1 = ProjectUtils.findClosestProject(path1, project1);
		Project closestProjectOfProject2 = findModuleByRelativePath(project2,
				closestProjectOfProject1.getRelativePath());

		if (closestProjectOfProject2 == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("relative path {} of project2 {} doesn't exist",
						closestProjectOfProject1.getRelativePath(), project2.getPath());
			}
			return null;
		}

		String path2 = path1.replace(closestProjectOfProject1.getPath(), closestProjectOfProject2.getPath());
		if (!new File(path2).exists()) {
			if (logger.isDebugEnabled()) {
				logger.debug("file of path2 {} doesn't exist", path2);
			}
			return null;
		}
		return path2;
	}

	private Project findModuleByRelativePath(Project project, String relativePath) {
		Map<String, Project> allProjectsMap = ProjectUtils.getAllProjectsMap(project);
		for (Project value : allProjectsMap.values()) {
			if (StringUtils.equals(value.getRelativePath(), relativePath))
				return value;
		}
		return null;
	}

	public void compare(ProjectCompare projectCompare, List<String> filePaths) {
		Project project1 = projectCompare.getProject1();
		Project project2 = projectCompare.getProject2();

		if (logger.isDebugEnabled()) {
			logger.debug("start to compare project1:{} and project2:{} with these files{}", project1.getPath(),
					project2.getPath(), filePaths);
		}

		for (String filePath : filePaths) {
			FileCompare fileCompare = new FileCompare();

			FileInformation fileInformation = fileInformationRepository.findByFilePath(filePath);
			if (fileInformation.getVersion().equals(project1.getVersion())) {
				fileCompare.setFilePath1(fileInformation.getFilePath());
				String anotherFilePath = getAnotherPath(project1, project2, filePath);
				fileCompare.setFilePath2(anotherFilePath);
			} else {
				fileCompare.setFilePath2(fileInformation.getFilePath());
				String anotherFilePath = getAnotherPath(project2, project1, filePath);
				fileCompare.setFilePath1(anotherFilePath);
			}
			if (!projectCompare.getFileCompares().contains(fileCompare)) {
				fileComparator.compare(fileCompare, project1.getVersion(), project2.getVersion());
				projectCompare.getFileCompares().add(fileCompare);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("compare project1:{} and project2:{} with these files{} finished", project1.getPath(),
					project2.getPath(), filePaths);
		}
	}
}
