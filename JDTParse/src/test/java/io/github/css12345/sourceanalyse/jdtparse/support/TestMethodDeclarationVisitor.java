package io.github.css12345.sourceanalyse.jdtparse.support;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.jdtparse.ParamsPropertyConfig;
import io.github.css12345.sourceanalyse.jdtparse.entity.MethodInformation;

@SpringJUnitConfig(ParamsPropertyConfig.class)
public class TestMethodDeclarationVisitor {

	private ASTParser parser;

	@Autowired
	private Params params;

	@Test
	public void test() {

		File projectFile = new File(
				"D:/Users/lenovo/workspace/Fruit-Sales-Platform/src/main/java/com/fruitsalesplatform/controller");
		File[] listFiles = projectFile.listFiles();

		try {
			for (File file : listFiles) {
				String content = FileUtils.readFileToString(file, "UTF-8");

				parser = ASTParser.newParser(AST.JLS8);
				parser.setSource(content.toCharArray());
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
				parser.setResolveBindings(true);
				parser.setUnitName(file.getAbsolutePath().substring("D:/Users/lenovo/workspace".length()));
				String[] sourcepathEntries = { "D:/Users/lenovo/workspace/Fruit-Sales-Platform/src/main/java" };
				String[] classpathEntries = {
						"D:/Program Files/apache-maven-3.5.4/commons-logging/commons-logging/1.2/commons-logging-1.2.jar",
						"D:/Program Files/apache-maven-3.5.4/com/alibaba/fastjson/1.1.41/fastjson-1.1.41.jar",
						"D:/Program Files/apache-maven-3.5.4/log4j/log4j/1.2.17/log4j-1.2.17.jar",
						"D:/Program Files/apache-maven-3.5.4/org/springframework/spring-context/4.2.5.RELEASE/spring-context-4.2.5.RELEASE.jar",
						"D:/Program Files/apache-maven-3.5.4/org/springframework/spring-web/4.2.5.RELEASE/spring-web-4.2.5.RELEASE.jar",
						"D:/Program Files/apache-maven-3.5.4/org/springframework/spring-beans/4.2.5.RELEASE/spring-beans-4.2.5.RELEASE.jar",
						"D:/Program Files/apache-maven-3.5.4/mysql/mysql-connector-java/5.1.29/mysql-connector-java-5.1.29.jar",
						"D:/Program Files/apache-maven-3.5.4/javax/javaee-api/7.0/javaee-api-7.0.jar",
						"D:/Program Files/apache-maven-3.5.4/net/gplatform/Sdk4J/2.0/Sdk4J-2.0.jar" };
				parser.setEnvironment(classpathEntries, sourcepathEntries, new String[] { "UTF-8" }, true);

				CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
				MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
				visitor.setIncludedNodeTypes(
						new HashSet<>(params.getMethodDeclarationIncludeTypes()));
				visitor.setValidPackageNames(new HashSet<>(new ArrayList<>(Arrays.asList("com.fruitsalesplatform"))));
				compilationUnit.accept(visitor);

				for (MethodInformation methodInformation : visitor.getMethodInformations()) {
					System.out.println("file: " + file.getName() + " method: "
							+ methodInformation.getMethodBinding().getName());
					// printMethodInvocations(methodInformation);

					printNodesAndEdges(methodInformation);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printMethodInvocations(MethodInformation methodInformation) {
		System.out.println("callMethodInvocations:");
		for (MethodInvocation methodInvocation : methodInformation.getMethodInvocations())
			System.out.println(methodInvocation);
		System.out.println();
	}

	public static void printNodesAndEdges(MethodInformation methodInformation) {
		System.out.println("nodes:" + methodInformation.getNodes().size());
		System.out.println("edges:");
		for (int i = 0; i < methodInformation.getEdges().size(); i++) {
			System.out.println("node " + i + " " + methodInformation.getNodes().get(i) + " "
					+ methodInformation.getNodes().get(i).getClass().getSimpleName());

			System.out.println(methodInformation.getEdges().get(i));
			System.out.println();
		}
	}
}
