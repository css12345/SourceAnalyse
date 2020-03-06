package io.github.css12345.sourceanalyse.jdtparse.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

/**
 * provide information for org.eclipse.jdt.core.dom.ASTParser
 */
public class ClassInformation {
	/**
	 * the absolute path of a .java file
	 */
	private String path;

	/**
	 * the file content of {@link #path}
	 */
	private String content;

	/**
	 * @see org.eclipse.jdt.core.dom.ASTParser#setUnitName(String unitName)
	 */
	private String unitName;

	/**
	 * @see org.eclipse.jdt.core.dom.ASTParser#setEnvironment(String[]
	 *      classpathEntries, String[] sourcepathEntries, String[] encodings,
	 *      boolean includeRunningVMBootclasspath)
	 */
	private String[] sourcepathEntries;

	/**
	 * @see org.eclipse.jdt.core.dom.ASTParser#setEnvironment(String[]
	 *      classpathEntries, String[] sourcepathEntries, String[] encodings,
	 *      boolean includeRunningVMBootclasspath)
	 */
	private String[] encodings;

	/**
	 * @see org.eclipse.jdt.core.dom.ASTParser#setEnvironment(String[]
	 *      classpathEntries, String[] sourcepathEntries, String[] encodings,
	 *      boolean includeRunningVMBootclasspath)
	 */
	private String[] classpathEntries;

	/**
	 * @see io.github.css12345.sourceanalyse.jdtparse.support.MethodInvocationVisitor#wantedPackageNames
	 */
	private Set<String> wantedPackageNames;

	public ClassInformation() {

	}

	public ClassInformation(String path, Project project) {
		this.path = path;
		try {
			this.content = FileUtils.readFileToString(new File(path), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(String.format("read file of path %s occur an IOException", path));
		}

		this.classpathEntries = project.getPathOfDependencies().toArray(new String[0]);
		this.wantedPackageNames = project.getWantedPackageNames();

		String projectPath = project.getPath();
		File projectFile = new File(projectPath);
		String projectName = projectFile.getName();
		int indexOfProjectName = path.indexOf(File.separator + projectName);
		this.unitName = path.substring(indexOfProjectName);

		List<String> sourcepaths = new ArrayList<>();
		String src = projectPath + String.format("%csrc", File.separatorChar);
		String src_main_java = projectPath
				+ String.format("%csrc%cmain%cjava", File.separatorChar, File.separatorChar, File.separatorChar);
		String src_test_java = projectPath
				+ String.format("%csrc%ctest%cjava", File.separatorChar, File.separatorChar, File.separatorChar);
		if (new File(src).exists()) {
			boolean src_main_java_exists = new File(src_main_java).exists();
			boolean src_test_java_exists = new File(src_test_java).exists();
			if (src_main_java_exists)
				sourcepaths.add(src_main_java);
			if (src_test_java_exists)
				sourcepaths.add(src_test_java);
			
			if (!src_main_java_exists && !src_test_java_exists)
				sourcepaths.add(src);
		}
		
		
		this.sourcepathEntries = sourcepaths.toArray(new String[0]);
		this.encodings = new String[sourcepathEntries.length];
		for (int i = 0; i < encodings.length; i++) {
			encodings[i] = "UTF-8";
		}
	}

	public String getPath() {
		return path;
	}

	public String getContent() {
		return content;
	}

	public String getUnitName() {
		return unitName;
	}

	public String[] getSourcepathEntries() {
		return sourcepathEntries;
	}

	public String[] getEncodings() {
		return encodings;
	}

	public String[] getClasspathEntries() {
		return classpathEntries;
	}

	public Set<String> getWantedPackageNames() {
		return wantedPackageNames;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public void setSourcepathEntries(String[] sourcepathEntries) {
		this.sourcepathEntries = sourcepathEntries;
	}

	public void setEncodings(String[] encodings) {
		this.encodings = encodings;
	}

	public void setClasspathEntries(String[] classpathEntries) {
		this.classpathEntries = classpathEntries;
	}

	public void setWantedPackageNames(Set<String> wantedPackageNames) {
		this.wantedPackageNames = wantedPackageNames;
	}

	@Override
	public String toString() {
		return "ClassInformation [path=" + path + ", unitName=" + unitName
				+ ", sourcepathEntries=" + Arrays.toString(sourcepathEntries) + ", encodings="
				+ Arrays.toString(encodings) + ", classpathEntries=" + Arrays.toString(classpathEntries)
				+ ", wantedPackageNames=" + wantedPackageNames + "]";
	}

}
