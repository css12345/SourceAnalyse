package io.github.css12345.sourceanalyse.persistence.support;

import java.io.File;

import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformationDTO;

/**
 * use this to read FileInformationDTO object from json file and write FileInformationDTO object to json file
 */
public interface FileInformationDTOMapper {
	FileInformationDTO readFromJSONFile(File jsonFile);
	
	/**
	 * write json object to {@link FileInformationDTO#filePath}
	 */
	void writeToJSONFile(FileInformationDTO fileInformationDTO);
}
