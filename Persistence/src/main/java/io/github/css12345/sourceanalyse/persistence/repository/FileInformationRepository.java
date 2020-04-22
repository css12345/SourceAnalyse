package io.github.css12345.sourceanalyse.persistence.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;

public interface FileInformationRepository extends Neo4jRepository<FileInformation, String> {

	FileInformation findByFilePath(String filePath);
}
