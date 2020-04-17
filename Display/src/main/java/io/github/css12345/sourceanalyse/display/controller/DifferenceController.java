package io.github.css12345.sourceanalyse.display.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.css12345.sourceanalyse.display.dao.DifferenceDao;
import io.github.css12345.sourceanalyse.display.dao.ProjectDao;
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
	public List<FileCompare> compareFiles(@RequestBody Map<String, Object> map) {
		String projectId1 = (String) map.get("projectId1");
		String projectId2 = (String) map.get("projectId2");
		List<String> filePaths = (List<String>) map.get("filePaths");
		int currentPage = (int) map.get("currentPage");
		int pageSize = (int) map.get("size");
		Project project1 = projectDao.getProjectById(projectId1);
		Project project2 = projectDao.getProjectById(projectId2);
		
		int startItem = (currentPage - 1) * pageSize;
		int toIndex = Math.min(startItem + pageSize, filePaths.size());
		List<String> pageFilePaths = filePaths.subList(startItem, toIndex);
		
		return differenceDao.compareFiles(project1, project2, pageFilePaths);
	}

}
