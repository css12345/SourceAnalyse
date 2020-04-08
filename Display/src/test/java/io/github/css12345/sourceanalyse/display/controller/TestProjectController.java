package io.github.css12345.sourceanalyse.display.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.display.SourceAnalyseApplication;
import io.github.css12345.sourceanalyse.display.dao.ProjectDao;
import io.github.css12345.sourceanalyse.display.entity.ProjectVO;
import io.github.css12345.sourceanalyse.display.entity.ProjectVOWrapper;

@SpringJUnitConfig
public class TestProjectController {
	@TestConfiguration
	@Import(SourceAnalyseApplication.class)
	public static class Config {

	}

	@Autowired
	private ProjectController projectController;
	
	@Autowired
	private ProjectDao projectDao;
	
	@Test
	public void testReadAll() {
		List<ProjectVOWrapper> projectVOs = projectDao.getAll();
		System.out.println(projectVOs.size());
	}

	@Test
	public void testResolveAndSetOtherFields() {
		ProjectVO projectVO1 = new ProjectVO();
		projectVO1.setProjectPath("D:\\Users\\cs\\Documents\\GitHub\\oim-fx\\oim-client-parent");
		projectVO1.setVersion("0.0.1");
		projectVO1.setProjectName("oim-fx");
		projectVO1.setWantedPackageNames(new HashSet<String>(new ArrayList<>(Arrays.asList("com.only", "com.oim", "com.onlyxiahui"))));
		projectController.resolveAndSetOtherFields(projectVO1);
		
		projectDao.save(projectVO1);
		
		ProjectVO projectVO2 = new ProjectVO();
		projectVO2.setProjectPath("D:\\tmp\\fastjson\\1.1.44\\fastjson-1.1.44");
		projectVO2.setVersion("1.1.44");
		projectVO2.setProjectName("fastjson");
		projectVO2.setWantedPackageNames(new HashSet<String>(new ArrayList<>(Arrays.asList("com.alibaba.fastjson"))));
		projectController.resolveAndSetOtherFields(projectVO2);
		
		projectDao.save(projectVO2);
	}
}
