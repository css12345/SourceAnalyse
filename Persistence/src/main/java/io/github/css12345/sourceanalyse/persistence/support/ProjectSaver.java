package io.github.css12345.sourceanalyse.persistence.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformationDTO;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.jdtparse.support.FileInformationResolver;
import io.github.css12345.sourceanalyse.jdtparse.utils.ProjectUtils;
import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;
import io.github.css12345.sourceanalyse.persistence.repository.FileInformationRepository;
import io.github.css12345.sourceanalyse.persistence.utils.ConverterUtils;

@Component
public class ProjectSaver {

	@Autowired
	private FileInformationResolver fileInformationResolver;

	@Autowired
	private FileInformationDTOMapper fileInformationDTOMapper;

	@Autowired
	private FileInformationRepository fileInformationRepository;

	@Autowired
	private ConverterUtils converterUtils;

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * every time convert and save how many files to the database
	 */
	private int size = 10;

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return this.size;
	}

	public void saveProject(Project project, boolean jsonFileSaved) {
		if (!jsonFileSaved) {
			saveToJsonFile(project);
		}

		convertAndSaveFileToDatabase(project);
	}

	protected void convertAndSaveFileToDatabase(Project project) {
		List<File> filesOfProject = ProjectUtils.findSuffixLikeJavaFiles(project);

		long startTime = System.currentTimeMillis();
		List<FileInformation> fileInformations = new ArrayList<>(size);
		for (int i = 0; i <= filesOfProject.size() / size; i++) {
			fileInformations.clear();
			long currentTimeStartTime = System.currentTimeMillis();
			for (int j = i * size; j < Math.min((i + 1) * size, filesOfProject.size()); j++) {
				fileInformations.add(converterUtils.convert(filesOfProject.get(j).getAbsolutePath()));
			}
			fileInformationRepository.saveAll(fileInformations);
			
			String dealMessage = String.format("handle data of [%d,%d), cost time is %d ms", i * size,
					Math.min((i + 1) * size, filesOfProject.size()), System.currentTimeMillis() - currentTimeStartTime);
			if (logger.isDebugEnabled())
				logger.debug(dealMessage);
		}
		if (logger.isInfoEnabled())
			logger.info("save to database cost time {} ms", System.currentTimeMillis() - startTime);
	}

	protected void saveToJsonFile(Project project) {
		long strartTime = System.currentTimeMillis();
		List<io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation> fileInformations = fileInformationResolver
				.getFileInformations(project);
		if (logger.isInfoEnabled()) {
			logger.info("file information resolve finished, cost {} ms", System.currentTimeMillis() - strartTime);
		}
		
		strartTime = System.currentTimeMillis();
		for (io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation fileInformation : fileInformations) {
			FileInformationDTO fileInformationDTO = new FileInformationDTO(fileInformation);
			fileInformationDTOMapper.writeToJSONFile(fileInformationDTO);
		}
		if (logger.isInfoEnabled()) {
			logger.info("save to json file finished, cost {} ms", System.currentTimeMillis() - strartTime);
		}
	}
}
