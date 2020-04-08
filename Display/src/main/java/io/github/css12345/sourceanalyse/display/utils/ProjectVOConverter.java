package io.github.css12345.sourceanalyse.display.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.css12345.sourceanalyse.display.entity.ProjectVO;
import io.github.css12345.sourceanalyse.display.entity.ProjectVOWrapper;

public class ProjectVOConverter {
	public static List<ProjectVOWrapper> convertFrom(ProjectVO projectVO) {
		List<ProjectVOWrapper> projectVOWrappers = new ArrayList<>();
		ProjectVOWrapper projectVOWrapper = new ProjectVOWrapper();
		projectVOWrappers.add(projectVOWrapper);
		projectVOWrapper.setProjectName(projectVO.getProjectName());
		projectVOWrapper.setProjectPath(projectVO.getProjectPath());
		projectVOWrapper.setPathOfDependencies(projectVO.getPathOfDependencies());
		projectVOWrapper.setRelativePath(projectVO.getRelativePath());
		projectVOWrapper.setVersion(projectVO.getVersion());
		projectVOWrapper.setWantedPackageNames(projectVO.getWantedPackageNames());
		projectVOWrapper.setParentProjectPath(projectVO.getParentProjectPath());
		String id = ProjectIDUtils.getIDByProjectPath(projectVOWrapper.getProjectPath());
		if (id == null)
			id = ProjectIDUtils.generateAndSaveID(projectVOWrapper.getProjectPath());
		projectVOWrapper.setId(id);
		projectVOWrapper.setPid(ProjectIDUtils.getIDByProjectPath(projectVOWrapper.getParentProjectPath()));
		
		for (ProjectVO moduleVO : projectVO.getModules()) {
			projectVOWrappers.addAll(convertFrom(moduleVO));
		}
		
		return projectVOWrappers;
	}
	
	public static List<ProjectVO> convertTo(List<ProjectVOWrapper> projectVOWrappers) {
		Map<String, ProjectVO> projectPathAndProjectVOMap = new HashMap<>();
		for (ProjectVOWrapper projectVOWrapper : projectVOWrappers) {
			ProjectVO projectVO = new ProjectVO();
			projectVO.setProjectName(projectVOWrapper.getProjectName());
			projectVO.setProjectPath(projectVOWrapper.getProjectPath());
			projectVO.setPathOfDependencies(projectVOWrapper.getPathOfDependencies());
			projectVO.setRelativePath(projectVOWrapper.getRelativePath());
			projectVO.setVersion(projectVOWrapper.getVersion());
			projectVO.setWantedPackageNames(projectVOWrapper.getWantedPackageNames());
			projectVO.setParentProjectPath(projectVOWrapper.getParentProjectPath());
			projectPathAndProjectVOMap.put(projectVO.getProjectPath(), projectVO);
		}
		
		List<ProjectVO> projectVOs = new ArrayList<>();
		for (ProjectVO projectVO : projectPathAndProjectVOMap.values()) {
			String parentProjectPath = projectVO.getParentProjectPath();
			if (parentProjectPath != null) {
				if (!projectPathAndProjectVOMap.containsKey(parentProjectPath))
					throw new RuntimeException("can't find " + parentProjectPath + " from projectVOWrappers");
				ProjectVO parentProjectVO = projectPathAndProjectVOMap.get(parentProjectPath);
				parentProjectVO.getModules().add(projectVO);
			} else {
				projectVOs.add(projectVO);
			}
		}
		return projectVOs;
	}
}
