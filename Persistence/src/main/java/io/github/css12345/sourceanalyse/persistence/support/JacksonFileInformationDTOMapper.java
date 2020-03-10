package io.github.css12345.sourceanalyse.persistence.support;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformationDTO;
import io.github.css12345.sourceanalyse.persistence.exception.JsonHandleException;

@Component
public class JacksonFileInformationDTOMapper implements FileInformationDTOMapper {

	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public FileInformationDTO readFromJSONFile(File jsonFile) {
		try {
			return objectMapper.readValue(jsonFile, FileInformationDTO.class);
		} catch (IOException e) {
			throw new JsonHandleException("an exception occur when read value from " + jsonFile, e);
		}
	}

	@Override
	public void writeToJSONFile(FileInformationDTO fileInformationDTO) {
		String filePath = fileInformationDTO.getFilePath();
		filePath = filePath.replace(".java", ".json");
		try {
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			objectMapper.writeValue(new File(filePath), fileInformationDTO);
		} catch (IOException e) {
			throw new JsonHandleException("an exception occur when write value to " + filePath, e);
		}
	}

}
