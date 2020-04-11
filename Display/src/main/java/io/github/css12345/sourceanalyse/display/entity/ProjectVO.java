package io.github.css12345.sourceanalyse.display.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;

import io.github.css12345.sourceanalyse.display.annotation.FileExistConstraint;
import io.github.css12345.sourceanalyse.display.annotation.ProjectAddConstraint;
import io.github.css12345.sourceanalyse.jdtparse.entity.ClassInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.utils.ASTParserUtils;
import io.github.css12345.sourceanalyse.jdtparse.utils.ProjectUtils;

public class ProjectVO {
	/**
	 * @see Project#path
	 */
	@NotEmpty
	@FileExistConstraint(message = "文件必须存在")
	@ProjectAddConstraint(message = "项目已被添加过")
	private String projectPath;

	@NotEmpty
	private String projectName;

	/**
	 * @see Project#relativePath
	 */
	private String relativePath;

	@NotEmpty
	private String version;

	/**
	 * @see Project#pathOfDependencies
	 */
	private List<String> pathOfDependencies = new ArrayList<>();

	/**
	 * @see Project#wantedPackageNames
	 */
	private Set<String> wantedPackageNames = new HashSet<>();
	
	private String inputWantedPackageNames;
	
	private String inputPathOfDependencies;
	
	private String parentProjectPath;

	private List<ProjectVO> modules = new ArrayList<>();
	
	private Map<String, String> classQualifiedNameLocationMap = new HashMap<>();

	public ProjectVO() {

	}

	public String getInputWantedPackageNames() {
		return inputWantedPackageNames;
	}

	public void setInputWantedPackageNames(String inputWantedPackageNames) {
		this.inputWantedPackageNames = inputWantedPackageNames;
	}

	public String getInputPathOfDependencies() {
		return inputPathOfDependencies;
	}

	public void setInputPathOfDependencies(String inputPathOfDependencies) {
		this.inputPathOfDependencies = inputPathOfDependencies;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<String> getPathOfDependencies() {
		return pathOfDependencies;
	}

	public void setPathOfDependencies(List<String> pathOfDependencies) {
		this.pathOfDependencies = pathOfDependencies;
	}

	public Set<String> getWantedPackageNames() {
		return wantedPackageNames;
	}

	public void setWantedPackageNames(Set<String> wantedPackageNames) {
		this.wantedPackageNames = wantedPackageNames;
	}

	public List<ProjectVO> getModules() {
		return modules;
	}

	public void setModules(List<ProjectVO> modules) {
		this.modules = modules;
	}
	
	public void resolveAndSetClassQualifiedNameLocationMap (Project project) {
		List<File> suffixLikeJavaFiles = ProjectUtils.findSuffixLikeJavaFiles(project);
		
		List<ClassInformation> classInformations = new ArrayList<>();
		for (File file : suffixLikeJavaFiles) {
			Project closestProject;
			try {
				closestProject = ProjectUtils.findClosestProject(file.getCanonicalPath(), project);
				if (closestProject == null)
					throw new RuntimeException("can't find a project for file " + file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			ClassInformation classInformation = new ClassInformation(file.getAbsolutePath(), closestProject);
			classInformations.add(classInformation);
		}
		
		for (ClassInformation classInformation : classInformations) {
			CompilationUnit compilationUnit = ASTParserUtils.setUpCompilationUnit(classInformation);
			compilationUnit.accept(new ASTVisitor() {
				@Override
				public void postVisit(ASTNode astNode) {
					if (astNode instanceof AbstractTypeDeclaration) {
						AbstractTypeDeclaration abstractTypeDeclaration = (AbstractTypeDeclaration) astNode;
						String classQualifiedName = abstractTypeDeclaration.resolveBinding().getQualifiedName();
						if (!classQualifiedName.equals(""))
							classQualifiedNameLocationMap.put(classQualifiedName, classInformation.getPath());
					}
				}
			});
		}
	}

	public void resolveAndSetModules(Project project) {
		List<Project> modules = project.getModules();
		for (Project module : modules) {
			ProjectVO moduleVO = new ProjectVO();
			moduleVO.setProjectName(projectName);
			moduleVO.setVersion(version);
			moduleVO.setWantedPackageNames(wantedPackageNames);
			moduleVO.setProjectPath(module.getPath());
			moduleVO.setPathOfDependencies(module.getPathOfDependencies());
			moduleVO.setRelativePath(module.getRelativePath());
			moduleVO.setParentProjectPath(project.getPath());
			moduleVO.setClassQualifiedNameLocationMap(classQualifiedNameLocationMap);
			moduleVO.resolveAndSetModules(module);
			this.modules.add(moduleVO);
		}
	}
	
	public void resolveAndSetInputWantedPackageNames() {
		if (inputWantedPackageNames == null || "".equals(inputWantedPackageNames.trim()))
			return;
		inputWantedPackageNames = inputWantedPackageNames.trim();
		String[] names = inputWantedPackageNames.split(",");
		for (String name : names) {
			name = name.trim();
			if (!"".equals(name))
				wantedPackageNames.add(name);
		}
	}
	
	public void resolveAndSetInputPathOfDependencies() {
		if (inputPathOfDependencies == null || "".equals(inputPathOfDependencies.trim()))
			return;
		inputPathOfDependencies = inputPathOfDependencies.trim();
		String[] dependencies = inputPathOfDependencies.split("\n");
		for (String dependency : dependencies) {
			dependency = dependency.trim();
			if (!"".equals(dependency))
				pathOfDependencies.add(dependency);
		}
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getParentProjectPath() {
		return parentProjectPath;
	}

	public void setParentProjectPath(String parentProjectPath) {
		this.parentProjectPath = parentProjectPath;
	}

	public Map<String, String> getClassQualifiedNameLocationMap() {
		return classQualifiedNameLocationMap;
	}

	public void setClassQualifiedNameLocationMap(Map<String, String> classQualifiedNameLocationMap) {
		this.classQualifiedNameLocationMap = classQualifiedNameLocationMap;
	}
}
