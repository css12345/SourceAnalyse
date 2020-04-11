package io.github.css12345.sourceanalyse.display.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.css12345.sourceanalyse.display.dao.DifferenceDao;
import io.github.css12345.sourceanalyse.display.dao.ProjectDao;
import io.github.css12345.sourceanalyse.display.entity.ProjectVOWrapper;
import io.github.css12345.sourceanalyse.display.utils.ProjectVOConverter;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;

@Controller
public class DifferenceController {

	@Autowired
	private DifferenceDao differenceDao;

	@Autowired
	private ProjectDao projectDao;

	@GetMapping("differences")
	public String getDifferences() {
		return "differences";
	}

	@ResponseBody
	@PostMapping("/differences/compare")
	public List<FileCompare> compareFiles(String projectId1, String projectId2, List<String> filePaths) {
		Project project1 = getProjectById(projectId1);
		Project project2 = getProjectById(projectId2);
		return differenceDao.compareFiles(project1, project2, filePaths);
	}

	public Project getProjectById(String projectId) {
		List<ProjectVOWrapper> projectVOWrappers = projectDao.getAll();
		for (ProjectVOWrapper projectVOWrapper : projectVOWrappers) {
			if (projectVOWrapper.getId().equals(projectId)) {
				return ProjectVOConverter.convert(projectVOWrapper);
			}
		}
		throw new IllegalArgumentException("can't find project for id " + projectId);
	}

}
