package io.github.css12345.sourceanalyse.jdtparse.support;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.jdtparse.entity.ClassInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.MethodInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.utils.ASTParserUtils;
import io.github.css12345.sourceanalyse.jdtparse.utils.ProjectUtils;

@Component
public class DefaultFileInformationResolver implements FileInformationResolver {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Params params;

	@Override
	public List<FileInformation> getFileInformations(Project project) {
		List<File> suffixLikeJavaFiles = ProjectUtils.findSuffixLikeJavaFiles(project);

		if (logger.isInfoEnabled()) {
			logger.info("find suffix like java files {}", suffixLikeJavaFiles);
		}

		List<FileInformation> fileInformations = new ArrayList<>();
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
						FileInformation.getClassQualifiedNameLocationMap().put(classQualifiedName, classInformation.getPath());
					}
				}
			});
		}

		for (ClassInformation classInformation : classInformations) {
			if (logger.isDebugEnabled()) {
				logger.debug("start to resolve file {}", classInformation.getPath());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("class information is {}", classInformation);
			}
			List<MethodInformation> methodInformations = buildASTAndVisitMethodDeclarations(classInformation);

			FileInformation fileInformation = new FileInformation();
			fileInformation.setFile(new File(classInformation.getPath()));
			fileInformation.setRootProjectPath(project.getPath());
			fileInformation.setMethodInformations(methodInformations);
			fileInformations.add(fileInformation);

			if (logger.isDebugEnabled()) {
				logger.debug("resolved file information is {}", fileInformation);
			}
		}
		return fileInformations;
	}

	private List<MethodInformation> buildASTAndVisitMethodDeclarations(ClassInformation classInformation) {
		CompilationUnit compilationUnit = ASTParserUtils.setUpCompilationUnit(classInformation);

		MethodDeclarationVisitor methodDeclarationVisitor = new MethodDeclarationVisitor();
		methodDeclarationVisitor.setIncludedNodeTypes(readFromParams());
		methodDeclarationVisitor.setValidPackageNames(classInformation.getWantedPackageNames());

		compilationUnit.accept(methodDeclarationVisitor);

		return methodDeclarationVisitor.getMethodInformations();
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
