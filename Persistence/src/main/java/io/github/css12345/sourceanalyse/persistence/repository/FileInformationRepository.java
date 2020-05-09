package io.github.css12345.sourceanalyse.persistence.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;

public interface FileInformationRepository extends Neo4jRepository<FileInformation, String> {

	FileInformation findByFilePath(String filePath);
	
	List<FileInformation> findByRootProjectPath(String rootProjectPath);
	
	@Query("match(fileInformation:FileInformation) where fileInformation.rootProjectPath = {rootProjectPath} detach delete fileInformation")
	void deleteAllByRootProjectPath(String rootProjectPath);
}
