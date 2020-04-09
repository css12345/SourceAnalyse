package io.github.css12345.sourceanalyse.display.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.css12345.sourceanalyse.display.dao.ProjectDao;
import io.github.css12345.sourceanalyse.display.entity.ProjectVO;
import io.github.css12345.sourceanalyse.display.entity.ProjectVOWrapper;
import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;

@Controller
public class ProjectController {

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private Params params;

	@Autowired
	private GenericApplicationContext applicationContext;

	@GetMapping("projects")
	@ResponseBody
	public List<ProjectVOWrapper> getAllProjects() {
		return projectDao.getAll();
	}
	
	@GetMapping("projects/delete/{projectId}")
	@ResponseBody
	public void delete(@PathVariable String projectId) {
		projectDao.delete(projectId);
	}
	
	@PostMapping("projects/deleteMany")
	@ResponseBody
	public void deleteMany(@RequestBody Map<String, List<String>> map) {
		projectDao.delete(map.get("projectIds"));
	}

	@GetMapping({ "/home", "/"})
	public String getHome() {
		return "projects";
	}
	
	@GetMapping("projects/add")
	public String addProject(Model model) {
		model.addAttribute(new ProjectVO());
		return "add";
	}

	@PostMapping("/projects/add")
	public String addProject(@Valid ProjectVO projectVO, Errors errors) {
		if (errors.hasErrors())
			return "add";

		resolveAndSetOtherFields(projectVO);
		projectDao.save(projectVO);
		return "redirect:/home";
	}

	public void resolveAndSetOtherFields(ProjectVO projectVO) {
		projectVO.resolveAndSetInputPathOfDependencies();
		projectVO.resolveAndSetInputWantedPackageNames();
		
		Project project = new Project();
		project.setPath("file:" + projectVO.getProjectPath());
		project.setWantedPackageNames(projectVO.getWantedPackageNames());
		project.setPathOfDependencies(projectVO.getPathOfDependencies());
		applicationContext.setAllowBeanDefinitionOverriding(true);
		applicationContext.registerBean(Project.class, () -> project);
		params.resolveProjects();

		projectVO.setPathOfDependencies(project.getPathOfDependencies());
		projectVO.setRelativePath(project.getRelativePath());
		projectVO.resolveAndSetClassQualifiedNameLocationMap(project);
		projectVO.resolveAndSetModules(project);
	}
}
