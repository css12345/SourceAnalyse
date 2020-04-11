package io.github.css12345.sourceanalyse.display.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import io.github.css12345.sourceanalyse.similarityanalyse.utils.CacheUtils;

@Repository
public class ProjectDaoImpl implements ProjectDao, InitializingBean {

	private final String PROJECT_CACHE_FILEPATH = "cache/projects.json";
	
	private File cacheFile;
	
	@Autowired
	private Params params;

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
		ProjectIDUtils.remove(projectPath);
		ProjectIDUtils.saveToCacheFile();

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

}
