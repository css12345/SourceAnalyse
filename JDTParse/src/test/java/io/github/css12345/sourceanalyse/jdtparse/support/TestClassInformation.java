package io.github.css12345.sourceanalyse.jdtparse.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import io.github.css12345.sourceanalyse.jdtparse.entity.ClassInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;

public class TestClassInformation {

	@Test
	public void test() throws IOException {
		String path = "D:\\Users\\cs\\Documents\\GitHub\\SourceAnalyse\\JDTParse\\src\\main\\java\\io\\github\\css12345\\sourceanalyse\\jdtparse\\support\\MavenDependencyManager.java";
		Project project = new Project();
		project.setPath("D:\\Users\\cs\\Documents\\GitHub\\SourceAnalyse\\JDTParse");
		project.setWantedPackageNames(
				new HashSet<>(new ArrayList<>(Arrays.asList("io.github.css12345.sourceanalyse.jdtparse"))));
		File pathOfDependenciesFile = new ClassPathResource("project2Dependencies.txt").getFile();
		List<String> pathOfDependencies = FileUtils.readLines(pathOfDependenciesFile, "UTF-8");
		project.setPathOfDependencies(pathOfDependencies);
		ClassInformation classInformation = new ClassInformation(path, project);
		System.out.println(classInformation);

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(classInformation.getContent().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setUnitName(classInformation.getUnitName());
		parser.setResolveBindings(true);
		parser.setEnvironment(classInformation.getClasspathEntries(), classInformation.getSourcepathEntries(),
				classInformation.getEncodings(), true);

		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);

		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor();
		methodInvocationVisitor.setWantedPackageNames(classInformation.getWantedPackageNames());
		compilationUnit.accept(methodInvocationVisitor);

		assertEquals(18, methodInvocationVisitor.getWantedMethodInvocations().size());
	}
}
