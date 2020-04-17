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

	public void saveProject(Project project, List<File> files) {
		List<File> notSavedDatabaseFiles = getNotSavedFilePathsOfDatabase(files);
		if (notSavedDatabaseFiles.isEmpty())
			return;

		List<File> notSavedJSONFiles = getNotSavedJSONFiles(notSavedDatabaseFiles);
		if (notSavedDatabaseFiles.isEmpty())
			return;

		saveToJsonFile(project, notSavedJSONFiles);
		convertAndSaveFileToDatabase(project, notSavedDatabaseFiles);
	}

	private List<File> getNotSavedFilePathsOfDatabase(List<File> files) {
		List<File> notSavedDatabaseFiles = new ArrayList<>();
		for (File file : files) {
			FileInformation fileInformation = fileInformationRepository.findByFilePath(file.getAbsolutePath(), 1);
			if (fileInformation == null)
				notSavedDatabaseFiles.add(file);
		}
		return notSavedDatabaseFiles;
	}

	private List<File> getNotSavedJSONFiles(List<File> files) {
		List<File> notSavedJSONFiles = new ArrayList<>();
		for (File file : files) {
			String jsonFilePath = file.getAbsolutePath().replace(".java", ".json");
			if (!new File(jsonFilePath).exists())
				notSavedJSONFiles.add(file);
		}
		return notSavedJSONFiles;
	}

	public void saveProject(Project project) {
		saveProject(project, ProjectUtils.findSuffixLikeJavaFiles(project));
	}

	public void convertAndSaveFileToDatabase(Project project) {
		List<File> filesOfProject = ProjectUtils.findSuffixLikeJavaFiles(project);

		convertAndSaveFileToDatabase(project, filesOfProject);
	}

	public void convertAndSaveFileToDatabase(Project project, List<File> files) {
		for (File file : files) {
			converterUtils.convertAndSave(project, file.getAbsolutePath());
		}
	}

	public void saveToJsonFile(Project project) {
		saveToJsonFile(project, ProjectUtils.findSuffixLikeJavaFiles(project));
	}

	public void saveToJsonFile(Project project, List<File> files) {
		long strartTime = System.currentTimeMillis();
		List<io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation> fileInformations = fileInformationResolver
				.getFileInformations(files, project);
		if (logger.isInfoEnabled()) {
			logger.info("file information of files {} resolve finished, cost {} ms", files, System.currentTimeMillis() - strartTime);
		}

		strartTime = System.currentTimeMillis();
		for (io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation fileInformation : fileInformations) {
			FileInformationDTO fileInformationDTO = new FileInformationDTO(fileInformation,
					project.getClassQualifiedNameLocationMap());
			fileInformationDTOMapper.writeToJSONFile(fileInformationDTO);
		}
		if (logger.isInfoEnabled()) {
			logger.info("save to json files {} finished, cost {} ms", files, System.currentTimeMillis() - strartTime);
		}
	}
}
