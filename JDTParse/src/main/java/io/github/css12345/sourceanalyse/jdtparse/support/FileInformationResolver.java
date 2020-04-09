package io.github.css12345.sourceanalyse.jdtparse.support;

import java.io.File;
import java.util.List;

import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;

/**
 * Resolve all .java file in project.
 *
 */
public interface FileInformationResolver {
	/**
	 * @param project project will be resolved
	 * @return a list contains resolved file information
	 */
	List<FileInformation> getFileInformations(Project project);
	
	List<FileInformation> getFileInformations(List<File> files, Project project);
}
