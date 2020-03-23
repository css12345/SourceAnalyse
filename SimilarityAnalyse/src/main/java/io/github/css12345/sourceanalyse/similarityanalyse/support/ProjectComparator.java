package io.github.css12345.sourceanalyse.similarityanalyse.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
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
		Project project1 = projectCompare.getProject1();
		Project project2 = projectCompare.getProject2();
		
		if (logger.isInfoEnabled()) {
			logger.info("start to compare project1:{} and project2:{}", project1.getPath(), project2.getPath());
		}
		
		String version1 = new File(project1.getPath()).getParentFile().getName();
		String version2 = new File(project2.getPath()).getParentFile().getName();

		List<FileInformation> fileInformationsOfProject1 = fileInformationRepository.findByVersion(version1, 2);
		List<FileInformation> fileInformationsOfProject2 = fileInformationRepository.findByVersion(version2, 2);
		boolean[] compared = new boolean[fileInformationsOfProject2.size()];

		List<FileCompare> fileCompares = new ArrayList<>();
		// 遍历project1的所有fileInformation,对每个fileInformation1
		// 1.查找fileInformation1在project1的哪个project中,因为project1可能有多个模块,fileInformation1可能在其子模块
		// 2.根据1中找到的closestProjectOfProject1的相对路径在project2中查找对应的closestProjectOfProject2,分为找到和没找到两种情况
		// 没找到说明模块被删除，对应的fileInformation2为null;
		// 找到后对路径进行替换，将fileInformation1的路径path1的closestProjectOfProject1部分替换为closestProjectOfProject2
		// 替换后路径path2可能存在，也可能不存在
		// 如果path2不存在，说明文件被删除，对应的fileInformation2为null;
		// 如果path2存在，在fileInformationsOfProject2中查找路径等于path2的fileInformation，即fileInformation2
		// 找到后标记已找到，避免后面重复比较；没找到抛出异常；
		// 3.调用fileComparator.compare()进一步完善fileCompare信息，然后添加到fileCompares中
		for (FileInformation fileInformation1 : fileInformationsOfProject1) {
			String path1 = fileInformation1.getFilePath();
			Project closestProjectOfProject1 = ProjectUtils.findClosestProject(path1, project1);
			String relativePath = closestProjectOfProject1.getRelativePath();

			Project closestProjectOfProject2 = findModuleByRelativePath(project2, relativePath);
			FileInformation fileInformation2 = getFileInformation2(project2, fileInformationsOfProject2, compared,
					path1, closestProjectOfProject1, closestProjectOfProject2);

			FileCompare fileCompare = new FileCompare(fileInformation1, fileInformation2);
			fileComparator.compare(fileCompare);
			fileCompares.add(fileCompare);
		}

		// 可能存在新增文件的情况，这时遍历fileInformationsOfProject2，对没比较过的文件，说明是新增文件，设置fileInformation1为null，调用fileComparator.compare()进一步完善fileCompare信息，然后添加到fileCompares中
		for (int i = 0; i < fileInformationsOfProject2.size(); i++) {
			if (compared[i])
				continue;

			FileInformation fileInformation2 = fileInformationsOfProject2.get(i);
			FileCompare fileCompare = new FileCompare();
			fileCompare.setFileInformation1(null);
			fileCompare.setFileInformation2(fileInformation2);
			fileComparator.compare(fileCompare);
			fileCompares.add(fileCompare);
		}

		projectCompare.setFileCompares(fileCompares);
		
		if (logger.isInfoEnabled()) {
			logger.info("compare project1:{} and project2:{} finished", project1.getPath(), project2.getPath());
		}
	}

	private FileInformation getFileInformation2(Project project2, List<FileInformation> fileInformationsOfProject2,
			boolean[] compared, String path1, Project closestProjectOfProject1, Project closestProjectOfProject2) {
		if (closestProjectOfProject2 == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("relative path {} of project2 {} doesn't exist", closestProjectOfProject1.getRelativePath(), project2.getPath());
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

		for (int i = 0; i < fileInformationsOfProject2.size(); i++) {
			FileInformation fileInformation = fileInformationsOfProject2.get(i);
			if (!compared[i] && FilenameUtils.equals(path2, fileInformation.getFilePath())) {
				compared[i] = true;
				return fileInformation;
			}
		}

		throw new RuntimeException(String.format("can't find path %s in project %s", path2, project2.getPath()));
	}

	private Project findModuleByRelativePath(Project project, String relativePath) {
		Map<String, Project> allProjectsMap = ProjectUtils.getAllProjectsMap(project);
		for (Project value : allProjectsMap.values()) {
			if (StringUtils.equals(value.getRelativePath(), relativePath))
				return value;
		}
		return null;
	}
}
