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
	
	@Autowired
	private CSVBatchInserter csvBatchInserter;

	/**
	 * use this to batch parsing and saving files
	 */
	private int parseSize = 100;

	/**
	 * use this to batch save {@link FileInformation}
	 */
	private int saveToDatabaseSize = 100;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public void saveProject(Project project, List<File> files) {
		List<File> notSavedDatabaseFiles = getNotSavedFilePathsOfDatabase(files);
		if (notSavedDatabaseFiles.isEmpty())
			return;

		List<File> notSavedJSONFiles = getNotSavedJSONFiles(notSavedDatabaseFiles);
		if (notSavedDatabaseFiles.isEmpty())
			return;

		saveToJsonFile(project, notSavedJSONFiles);
		convertAndSaveFileToDatabase(notSavedDatabaseFiles);
	}

	private List<File> getNotSavedFilePathsOfDatabase(List<File> files) {
		List<File> notSavedDatabaseFiles = new ArrayList<>();
		for (File file : files) {
			FileInformation fileInformation = fileInformationRepository.findByFilePath(file.getAbsolutePath());
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

		convertAndSaveFileToDatabase(filesOfProject);
	}

	public void convertAndSaveFileToDatabase(List<File> files) {
		if (logger.isInfoEnabled()) {
			logger.info("prepare to convert and save files to database");
		}
		long allStartTime = System.currentTimeMillis();
		int parseTimes = (int) Math.ceil(files.size() / (saveToDatabaseSize * 1.0));
		for (int i = 0; i < parseTimes; i++) {
			long strartTime = System.currentTimeMillis();

			final int fromIndex = i * saveToDatabaseSize;
			final int toIndex = Math.min((i + 1) * saveToDatabaseSize, files.size());
			List<FileInformation> resolvedFileInformations = new ArrayList<>();
			for (int j = fromIndex; j < toIndex; j++) {
				FileInformation resolvedFileInformation = converterUtils.convert(files.get(j).getAbsolutePath());
				resolvedFileInformations.add(resolvedFileInformation);
			}

			if (logger.isInfoEnabled()) {
				logger.info("convert files[{}-{}) finished, cost {} ms", fromIndex, toIndex,
						System.currentTimeMillis() - strartTime);
			}
			csvBatchInserter.write(resolvedFileInformations);
			if (logger.isInfoEnabled()) {
				logger.info("save files[{}-{}) to csv file finished, cost {} ms", fromIndex, toIndex,
						System.currentTimeMillis() - strartTime);
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("convert and save files to csv file all cost {} ms", System.currentTimeMillis() - allStartTime);
		}
		
		long csvImportStartTime = System.currentTimeMillis();
		csvBatchInserter.executeCSVImport();
		if (logger.isInfoEnabled()) {
			logger.info("load from csv files to database cost {} ms", System.currentTimeMillis() - csvImportStartTime);
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("convert and save files to database all cost {} ms", System.currentTimeMillis() - allStartTime);
		}
	}

	public void saveToJsonFile(Project project) {
		saveToJsonFile(project, ProjectUtils.findSuffixLikeJavaFiles(project));
	}

	public void saveToJsonFile(Project project, List<File> files) {
		if (logger.isInfoEnabled()) {
			logger.info("prepare to resolve and save files");
		}
		long allStartTime = System.currentTimeMillis();
		int parseTimes = (int) Math.ceil(files.size() / (parseSize * 1.0));
		for (int i = 0; i < parseTimes; i++) {
			long strartTime = System.currentTimeMillis();
			List<File> subFiles = files.subList(i * parseSize, Math.min((i + 1) * parseSize, files.size()));
			List<io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation> fileInformations = fileInformationResolver
					.getFileInformations(subFiles, project);
			if (logger.isInfoEnabled()) {
				logger.info("file information of files[{}-{}) resolve finished, cost {} ms", i * parseSize,
						Math.min((i + 1) * parseSize, files.size()), System.currentTimeMillis() - strartTime);
			}

			strartTime = System.currentTimeMillis();
			for (io.github.css12345.sourceanalyse.jdtparse.entity.FileInformation fileInformation : fileInformations) {
				FileInformationDTO fileInformationDTO = new FileInformationDTO(fileInformation,
						project.getClassQualifiedNameLocationMap());
				fileInformationDTOMapper.writeToJSONFile(fileInformationDTO);
			}
			if (logger.isInfoEnabled()) {
				logger.info("save to json files[{}-{}) finished, cost {} ms", i * parseSize,
						Math.min((i + 1) * parseSize, files.size()), System.currentTimeMillis() - strartTime);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("save to json files all cost {} ms", System.currentTimeMillis() - allStartTime);
		}
	}

	public int getParseSize() {
		return parseSize;
	}

	public void setParseSize(int parseSize) {
		this.parseSize = parseSize;
	}

	public int getSaveToDatabaseSize() {
		return saveToDatabaseSize;
	}

	public void setSaveToDatabaseSize(int saveToDatabaseSize) {
		this.saveToDatabaseSize = saveToDatabaseSize;
	}
}
