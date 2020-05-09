package io.github.css12345.sourceanalyse.display.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.css12345.sourceanalyse.display.dao.ProjectDao;
import io.github.css12345.sourceanalyse.display.entity.ProjectVO;
import io.github.css12345.sourceanalyse.display.entity.ProjectVOWrapper;
import io.github.css12345.sourceanalyse.display.utils.ProjectIDUtils;
import io.github.css12345.sourceanalyse.display.utils.ProjectVOConverter;
import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.utils.ProjectUtils;
import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;
import io.github.css12345.sourceanalyse.persistence.repository.FileInformationRepository;
import io.github.css12345.sourceanalyse.persistence.repository.MethodRepository;
import io.github.css12345.sourceanalyse.similarityanalyse.utils.CacheUtils;
import io.github.css12345.sourceanalyse.similarityanalyse.utils.FileCompareCacheUtils;

@Repository
public class ProjectDaoImpl implements ProjectDao, InitializingBean {

	private final String PROJECT_CACHE_FILEPATH = "cache/projects.json";
	
	private File cacheFile;
	
	@Autowired
	private Params params;
	
	@Autowired
	private FileInformationRepository fileInformationRepository;
	
	@Autowired
	private MethodRepository methodRepository;

	@Override
	public List<ProjectVOWrapper> getAll() {
		return CacheUtils.readAllFromCacheFile(cacheFile, ProjectVOWrapper.class);
	}

	@Override
	public void save(ProjectVO project) {
		List<ProjectVOWrapper> projectVOWrappers = ProjectVOConverter.convertFrom(project);
		for (ProjectVOWrapper projectVOWrapper : projectVOWrappers) {
			CacheUtils.saveToCacheFile(cacheFile, projectVOWrapper, true);
		}
	}

	@Override
	public void delete(String projectId) {
		String projectPath = ProjectIDUtils.getProjectPathById(projectId);
		if (projectPath == null)
			return;
		FileCompareCacheUtils.deleteFileComparesForProject(projectId);
		deleteProjectInformationFromDatabase(projectPath);
		deleteResolvedJsonFile(projectId, projectPath);
		deleteProjectIDFromCacheFile(projectPath);
		deleteProjectWrappersFromCacheFile(projectId);
	}

	public void deleteResolvedJsonFile(String projectId, String projectPath) {
		Project project = getProjectById(projectId);
		List<File> files = ProjectUtils.findSuffixLikeJavaFiles(project);
		for (File file : files) {
			String jsonFilePath = file.getAbsolutePath().replace(".java", ".json");
			File jsonFile = new File(jsonFilePath);
			if (jsonFile.exists())
				jsonFile.delete();
		}
	}

	public void deleteProjectInformationFromDatabase(String projectPath) {
		List<FileInformation> allFileInformationsOfProject = fileInformationRepository.findByRootProjectPath(projectPath);
		for (FileInformation fileInformation : allFileInformationsOfProject) {
			methodRepository.deleteAllByFilePath(fileInformation.getFilePath());
		}
		
		fileInformationRepository.deleteAllByRootProjectPath(projectPath);
	}

	public void deleteProjectIDFromCacheFile(String projectPath) {
		ProjectIDUtils.remove(projectPath);
		ProjectIDUtils.saveToCacheFile();
	}

	public void deleteProjectWrappersFromCacheFile(String projectId) {
		List<ProjectVOWrapper> projectVOWrappers = getAll();
		try {
			FileUtils.writeStringToFile(cacheFile, "", "UTF-8", false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		for (ProjectVOWrapper projectVOWrapper : projectVOWrappers) {
			if (projectVOWrapper.getId().equals(projectId))
				continue;
			
			if (projectId.equals(projectVOWrapper.getPid())) {
				projectVOWrapper.setParentProjectPath(null);
				projectVOWrapper.setPid(null);
			}
			CacheUtils.saveToCacheFile(cacheFile, projectVOWrapper, true);
		}
	}

	@Override
	public void delete(Iterable<String> projectIds) {
		for (String projectId : projectIds) {
			delete(projectId);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		cacheFile = new File(params.getSITE_DATA_DIR(), PROJECT_CACHE_FILEPATH);
		File cacheDirectory = cacheFile.getParentFile();
		if (!cacheDirectory.exists())
			cacheDirectory.mkdirs();
		try {
			cacheFile.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> getFilesOfProject(String projectId) {
		Project project = getProjectById(projectId);
		List<File> filesOfProject = ProjectUtils.findSuffixLikeJavaFiles(project);
		return filesOfProject.stream().map(file -> file.getAbsolutePath()).collect(Collectors.toList());
	}

	@Override
	public Project getProjectById(String projectId) {
		List<ProjectVOWrapper> projectVOWrappers = getAll();
		String projectPath = null;
		for (ProjectVOWrapper projectVOWrapper : projectVOWrappers) {
			if (projectVOWrapper.getId().equals(projectId)) {
				projectPath = projectVOWrapper.getProjectPath();
				break;
			}
		}
		if (projectPath != null) {
			List<ProjectVO> projectVOs = ProjectVOConverter.convertTo(projectVOWrappers);
			Set<Project> projects = new HashSet<>();
			for (ProjectVO projectVO : projectVOs) {
				projects.addAll(ProjectUtils.getAllProjectsMap(ProjectVOConverter.convert(projectVO)).values());
			}
			for (Project project:projects) {
				if (projectPath.equals(project.getPath()))
					return project;
			}
		}
		throw new IllegalArgumentException("can't find project for id " + projectId);
	}
}
