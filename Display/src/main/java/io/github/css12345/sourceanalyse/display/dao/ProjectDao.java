package io.github.css12345.sourceanalyse.display.dao;

import java.util.List;

import io.github.css12345.sourceanalyse.display.entity.ProjectVO;
import io.github.css12345.sourceanalyse.display.entity.ProjectVOWrapper;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;

public interface ProjectDao {
	List<ProjectVOWrapper> getAll();
	
	void save(ProjectVO project);
	
	void delete(String projectId);
	
	void delete(Iterable<String> projectIds);

	List<String> getFilesOfProject(String projectId);

	Project getProjectById(String projectId);
}
