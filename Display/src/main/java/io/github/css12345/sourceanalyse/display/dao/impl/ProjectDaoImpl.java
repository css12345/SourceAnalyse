package io.github.css12345.sourceanalyse.display.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Repository;

import io.github.css12345.sourceanalyse.display.dao.ProjectDao;
import io.github.css12345.sourceanalyse.display.entity.ProjectVO;
import io.github.css12345.sourceanalyse.display.entity.ProjectVOWrapper;
import io.github.css12345.sourceanalyse.display.utils.CacheUtils;
import io.github.css12345.sourceanalyse.display.utils.ProjectIDUtils;
import io.github.css12345.sourceanalyse.display.utils.ProjectVOConverter;

@Repository
public class ProjectDaoImpl implements ProjectDao {

	private final String PROJECT_CACHE_FILEPATH = "cache/projects.json";

	@Override
	public List<ProjectVOWrapper> getAll() {
		File cacheFile = new File(System.getProperty("user.dir"), PROJECT_CACHE_FILEPATH);
		return CacheUtils.readAllFromCacheFile(cacheFile, ProjectVOWrapper.class);
	}

	@Override
	public void save(ProjectVO project) {
		File cacheFile = new File(System.getProperty("user.dir"), PROJECT_CACHE_FILEPATH);
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
		ProjectIDUtils.remove(projectPath);
		ProjectIDUtils.saveToCacheFile();

		List<ProjectVOWrapper> projectVOWrappers = getAll();
		File cacheFile = new File(System.getProperty("user.dir"), PROJECT_CACHE_FILEPATH);
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

}
