package io.github.css12345.sourceanalyse.jdtparse.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import io.github.css12345.sourceanalyse.jdtparse.entity.ClassInformation;

public class ASTParserUtils {
	private static Map<String, CompilationUnit> compilationUnitCache = new HashMap<>();
	
	public static CompilationUnit setUpCompilationUnit(ClassInformation classInformation) {
		if (compilationUnitCache.containsKey(classInformation.getPath()))
			return compilationUnitCache.get(classInformation.getPath());
		
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
		compilationUnitCache.put(classInformation.getPath(), compilationUnit);
		return compilationUnit;
	}
}
