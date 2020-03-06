package io.github.css12345.sourceanalyse.jdtparse.support;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.junit.jupiter.api.Test;

import io.github.css12345.sourceanalyse.jdtparse.support.MethodInvocationVisitor;

public class TestMethodInvocationVisitor {

	private ASTParser parser;

	@Test
	public void test1() throws IOException {
		String path = "D:/Users/lenovo/workspace/Fruit-Sales-Platform/src/main/java/com/fruitsalesplatform/controller/CommoditiesController.java";
		String content = FileUtils.readFileToString(new File(path), "UTF-8");

		parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(content.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setUnitName(
				"/Fruit-Sales-Platform/src/main/java/com/fruitsalesplatform/controller/CommoditiesController.java");
		String[] sourcepathEntries = { "D:/Users/lenovo/workspace/Fruit-Sales-Platform/src/main/java" };
		String[] classpathEntries = {
				"D:/Program Files/apache-maven-3.5.4/commons-logging/commons-logging/1.2/commons-logging-1.2.jar",
				"D:/Program Files/apache-maven-3.5.4/com/alibaba/fastjson/1.1.41/fastjson-1.1.41.jar",
				"D:/Program Files/apache-maven-3.5.4/log4j/log4j/1.2.17/log4j-1.2.17.jar",
				"D:/Program Files/apache-maven-3.5.4/org/springframework/spring-context/4.2.5.RELEASE/spring-context-4.2.5.RELEASE.jar" };
		parser.setEnvironment(classpathEntries, sourcepathEntries, new String[] { "UTF-8" }, true);

		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		MethodInvocationVisitor visitor = new MethodInvocationVisitor();
		visitor.setWantedPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("com.fruitsalesplatform"))));
		compilationUnit.accept(visitor);

		for (MethodInvocation methodInvocation : visitor.getWantedMethodInvocations()) {
			System.out.println(methodInvocation);
		}
	}

	@Test
	public void test2() throws IOException {		
		String path = "D:\\tmp\\0.0.1\\download\\src\\main\\java\\com\\example\\download\\Main.java";
		String content = FileUtils.readFileToString(new File(path), "UTF-8");

		parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(content.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setUnitName("/download/src/main/java/com/example/download/Main.java");
		String[] sourcepathEntries = { "D:\\tmp\\0.0.1\\download\\src\\main\\java" };
		String[] classpathEntries = {};
		parser.setEnvironment(classpathEntries, sourcepathEntries, new String[] { "UTF-8" }, true);

		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		MethodInvocationVisitor visitor = new MethodInvocationVisitor();
		visitor.setWantedPackageNames(Collections.emptySet());
		compilationUnit.accept(visitor);
		for (MethodInvocation methodInvocation : visitor.getWantedMethodInvocations()) {
			System.out.println(methodInvocation);
		}
	}

	@Test
	public void test3() throws IOException {
		String path = "D:\\tmp\\0.0.1\\download\\src\\main\\java\\com\\example\\download\\Main.java";
		String content = FileUtils.readFileToString(new File(path), "UTF-8");

		parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(content.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setUnitName("/download/src/main/java/com/example/download/DownloadManager.java");
		String[] sourcepathEntries = { "D:\\tmp\\0.0.1\\download\\src\\main\\java" };
		String[] classpathEntries = {};
		parser.setEnvironment(classpathEntries, sourcepathEntries, new String[] { "UTF-8" }, true);

		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		MethodInvocationVisitor visitor = new MethodInvocationVisitor();
		visitor.setWantedPackageNames(Collections.emptySet());
		compilationUnit.accept(visitor);

		for (MethodInvocation methodInvocation : visitor.getWantedMethodInvocations()) {
			System.out.println(methodInvocation);
		}
	}

	@Test
	public void test4() throws IOException {
		String path = "D:\\tmp\\0.0.1\\download\\src\\main\\java\\com\\example\\download\\Main.java";
		String content = FileUtils.readFileToString(new File(path), "UTF-8");

		parser = ASTParser.newParser(AST.JLS8);
		Map<String, String> options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setSource(content.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setUnitName("/download/src/test/java/download/Test1.java");
		String[] sourcepathEntries = { "D:\\tmp\\0.0.1\\download\\src\\main\\java", "D:\\tmp\\0.0.1\\download\\src\\test\\java" };
		String[] classpathEntries = {
				"D:\\Program Files\\apache-maven-3.5.4\\org\\junit\\jupiter\\junit-jupiter-api\\5.5.2\\junit-jupiter-api-5.5.2.jar" };
		parser.setEnvironment(classpathEntries, sourcepathEntries, new String[] { "UTF-8", "UTF-8" }, true);

		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		MethodInvocationVisitor visitor = new MethodInvocationVisitor();
		visitor.setWantedPackageNames(Collections.emptySet());
		compilationUnit.accept(visitor);

		for (MethodInvocation methodInvocation : visitor.getWantedMethodInvocations()) {
			System.out.println(methodInvocation);
		}
	}
}
