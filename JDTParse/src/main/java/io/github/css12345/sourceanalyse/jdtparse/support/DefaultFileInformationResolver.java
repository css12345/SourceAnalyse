package io.github.css12345.sourceanalyse.jdtparse.support;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.jdtparse.entity.ClassInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.MethodInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;

@Component
public class DefaultFileInformationResolver implements FileInformationResolver {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Params params;

	@Override
	public List<FileInformation> getFileInformations(Project project) {
		Map<String, Project> allProjects = new HashMap<>();
		addProjects(project, allProjects);
		
		File rootFile = new File(project.getPath());
		List<File> suffixLikeJavaFiles = new ArrayList<>();
		findSuffixLikeJavaFiles(rootFile, suffixLikeJavaFiles);
		//may be some module did not in root project directory
		for (String path : allProjects.keySet()) {
			if (!path.startsWith(project.getPath()))
				findSuffixLikeJavaFiles(new File(path), suffixLikeJavaFiles);
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("find suffix like java files {}", suffixLikeJavaFiles);
		}
		

		List<FileInformation> fileInformations = new ArrayList<>();
		for (File file : suffixLikeJavaFiles) {
			if (logger.isInfoEnabled()) {
				logger.info("start to resolve file {}", file);
			}

			Project closestProject;
			try {
				closestProject = findClosetProject(file.getCanonicalPath(), allProjects);
				if (closestProject == null)
					throw new RuntimeException("can't find a project for file " + file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			ClassInformation classInformation = new ClassInformation(file.getAbsolutePath(), closestProject);
			if (logger.isDebugEnabled()) {
				logger.debug("class information is {}", classInformation);
			}
			List<MethodInformation> methodInformations = buildASTAndVisitMethodDeclarations(classInformation);

			FileInformation fileInformation = new FileInformation();
			fileInformation.setFile(file);
			fileInformation.setRootProjectPath(project.getPath());
			fileInformation.setMethodInformations(methodInformations);
			fileInformations.add(fileInformation);

			if (logger.isInfoEnabled()) {
				logger.info("resolved file information is {}", fileInformation);
			}
		}
		return fileInformations;
	}

	private Project findClosetProject(String filePath, Map<String, Project> allProjects) {
		int lastIndexOfFileSeparator;
		while ((lastIndexOfFileSeparator = filePath.lastIndexOf(File.separatorChar)) != -1) {
			filePath = filePath.substring(0, lastIndexOfFileSeparator);
			if (allProjects.containsKey(filePath))
				return allProjects.get(filePath);
		}
		return null;
	}

	private void addProjects(Project project, Map<String, Project> allProjects) {
		allProjects.put(project.getPath(), project);
		for (Project subproject : project.getModules())
			addProjects(subproject, allProjects);
	}

	private List<MethodInformation> buildASTAndVisitMethodDeclarations(ClassInformation classInformation) {
		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setSource(classInformation.getContent().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setUnitName(classInformation.getUnitName());
		parser.setEnvironment(classInformation.getClasspathEntries(), classInformation.getSourcepathEntries(),
				classInformation.getEncodings(), true);

		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);

		MethodDeclarationVisitor methodDeclarationVisitor = new MethodDeclarationVisitor();
		methodDeclarationVisitor.setIncludedNodeTypes(readFromParams());
		methodDeclarationVisitor.setValidPackageNames(classInformation.getWantedPackageNames());

		compilationUnit.accept(methodDeclarationVisitor);
		
		compilationUnit.accept(new ASTVisitor() {
			@Override
			public boolean visit(TypeDeclaration typeDeclaration) {
				String classQualifiedName = typeDeclaration.resolveBinding().getQualifiedName();
				FileInformation.getClassQualifiedNameLocationMap().put(classQualifiedName, classInformation.getPath());
				return true;
			}
		});
		return methodDeclarationVisitor.getMethodInformations();
	}

	private void findSuffixLikeJavaFiles(File rootFile, List<File> suffixLikeJavaFiles) {
		File[] subFiles = rootFile.listFiles();
		for (File subFile : subFiles) {
			if (subFile.isDirectory())
				findSuffixLikeJavaFiles(subFile, suffixLikeJavaFiles);
			else if (subFile.getName().endsWith(".java") && subFile.getAbsolutePath().contains("src"))
				suffixLikeJavaFiles.add(subFile);
		}
	}

	private Set<String> readFromParams() {
		try {
			List<String> includeNodeTypes = FileUtils.readLines(params.getMethodDeclarationIncludeTypesFile(), "UTF-8");
			return new HashSet<>(includeNodeTypes);
		} catch (IOException e) {
			throw new RuntimeException(String.format("read file of path %s occur an IOException",
					params.getMethodDeclarationIncludeTypesFile()));
		}
	}
}