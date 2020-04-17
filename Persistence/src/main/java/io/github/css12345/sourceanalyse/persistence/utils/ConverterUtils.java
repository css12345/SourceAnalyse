package io.github.css12345.sourceanalyse.persistence.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.entity.ASTNodeInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformationDTO;
import io.github.css12345.sourceanalyse.jdtparse.entity.MethodInformationDTO;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;
import io.github.css12345.sourceanalyse.persistence.entity.ASTNode;
import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;
import io.github.css12345.sourceanalyse.persistence.entity.Method;
import io.github.css12345.sourceanalyse.persistence.repository.FileInformationRepository;
import io.github.css12345.sourceanalyse.persistence.repository.MethodRepository;
import io.github.css12345.sourceanalyse.persistence.support.FileInformationDTOMapper;
import io.github.css12345.sourceanalyse.persistence.support.ProjectSaver;

@Component
public class ConverterUtils {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FileInformationDTOMapper fileInformationDTOMapper;

	@Autowired
	private ProjectSaver projectSaver;

	@Autowired
	private FileInformationRepository fileInformationRepository;

	@Autowired
	private MethodRepository methodRepository;

	public FileInformation convertAndSave(Project project, String fileInformationDTOPath) {
		FileInformation cachedFileInformation = fileInformationRepository.findByFilePath(fileInformationDTOPath, 1);
		if (cachedFileInformation != null) {
			if (logger.isInfoEnabled()) {
				logger.info("find file information from database for key {}", fileInformationDTOPath);
			}
			return cachedFileInformation;
		}

		FileInformationDTO fileInformationDTO = getFileInformationDTO(project, fileInformationDTOPath);

		FileInformation fileInformation = new FileInformation(fileInformationDTO.getFilePath(),
				fileInformationDTO.getRootProjectPath(), fileInformationDTO.getVersion());
		for (MethodInformationDTO methodInformationDTO : fileInformationDTO.getHasMethods()) {
			Method method = convertAndSave(project, methodInformationDTO, true);
			fileInformation.addMethod(method);
		}

		fileInformationRepository.save(fileInformation);

		if (logger.isInfoEnabled()) {
			logger.info("create file information and save to database for key {}", fileInformationDTOPath);
		}
		return fileInformation;
	}

	public Method convertAndSave(Project project, MethodInformationDTO methodInformationDTO,
			boolean resolveInvocatedMethods) {
		String version = methodInformationDTO.getVersion();
		String briefMethodInformation = methodInformationDTO.getBriefMethodInformation();
		Method cachedMethod = methodRepository.findByBriefMethodInformationAndVersion(briefMethodInformation, version);
		Method method = null;
		if (cachedMethod != null) {
			if (logger.isInfoEnabled()) {
				logger.info("find method from database for briefMethodInformation {} and version {}",
						briefMethodInformation, version);
			}
			method = cachedMethod;
		} else {
			method = new Method(methodInformationDTO.getFilePath(), methodInformationDTO.getVersion(),
					methodInformationDTO.getBriefMethodInformation());
			method.setRootNode(generateAstNodes(methodInformationDTO));
			methodRepository.save(method);
			if (logger.isInfoEnabled()) {
				logger.info(
						"create method(no invocated methods) and save to database for briefMethodInformation {} and version {}",
						briefMethodInformation, version);
			}
		}

		if (resolveInvocatedMethods) {
			if (method.getMethodInvocations().size() == methodInformationDTO.getMethodInvocationsMap().size()) {
				if (logger.isInfoEnabled()) {
					logger.info(
							"method already resolved invocated methods for briefMethodInformation {} and version {}",
							briefMethodInformation, version);
				}
				return method;
			}

			if (method.getMethodInvocations().size() != 0) {
				String errorMessage = String.format(
						"method (briefMethodInformation = %s and version = %s) 's invocated methods size %d is neither 0 nor %d",
						briefMethodInformation, version, method.getMethodInvocations().size(), methodInformationDTO);
				throw new IllegalStateException(errorMessage);
			}

			Collection<String> methodInvocations = methodInformationDTO.getMethodInvocationsMap().values();
			for (String methodInvocationInformation : methodInvocations) {
				final String JAVA_SUFFIX = ".java";
				int indexOfFirstSign = methodInvocationInformation.indexOf(JAVA_SUFFIX);
				String location = methodInvocationInformation.substring(0, indexOfFirstSign) + JAVA_SUFFIX;
				String invocatedMethodBriefMethodInformation = methodInvocationInformation
						.substring(indexOfFirstSign + JAVA_SUFFIX.length() + 1);

				FileInformationDTO invocatedMethodFileInformationDTO = getFileInformationDTO(project, location);
				List<MethodInformationDTO> methodsOfInvocatedMethodFileInformationDTO = invocatedMethodFileInformationDTO
						.getHasMethods();
				for (MethodInformationDTO methodOfInvocatedMethodFileInformationDTO : methodsOfInvocatedMethodFileInformationDTO) {
					if (methodOfInvocatedMethodFileInformationDTO.getBriefMethodInformation()
							.equals(invocatedMethodBriefMethodInformation)) {
						method.invocate(convertAndSave(project, methodOfInvocatedMethodFileInformationDTO, false));
						break;
					}
				}
			}

			methodRepository.save(method);
			if (logger.isInfoEnabled()) {
				logger.info(
						"create method(with invocated methods) and save to database for briefMethodInformation {} and version {}",
						briefMethodInformation, version);
			}
		}
		return method;
	}

	private FileInformationDTO getFileInformationDTO(Project project, String fileInformationDTOPath) {
		if (!new File(fileInformationDTOPath.replace(".java", ".json")).exists()) {
			if (logger.isInfoEnabled()) {
				logger.info("file {} didn't resolve, start to resolve and save it to json file",
						fileInformationDTOPath);
			}
			projectSaver.saveToJsonFile(project, new ArrayList<>(Arrays.asList(new File(fileInformationDTOPath))));
		}
		FileInformationDTO fileInformationDTO = fileInformationDTOMapper
				.readFromJSONFile(new File(fileInformationDTOPath.replace(".java", ".json")));
		return fileInformationDTO;
	}

	private ASTNode generateAstNodes(MethodInformationDTO methodInformationDTO) {
		List<ASTNode> astNodes = new ArrayList<>();
		for (ASTNodeInformation astNodeInformation : methodInformationDTO.getNodes()) {
			ASTNode astNode = new ASTNode(astNodeInformation.getContent(), astNodeInformation.getType());
			astNodes.add(astNode);
		}

		for (int i = 0; i < methodInformationDTO.getEdges().size(); i++) {
			List<Integer> childs = methodInformationDTO.getEdges().get(i);
			for (Integer child : childs) {
				astNodes.get(i).addChild(astNodes.get(child));
				astNodes.get(child).setParentNode(astNodes.get(i));
			}
		}

		ASTNode rootNode = astNodes.get(astNodes.size() - 1);
		while (rootNode.getParentNode() != null) {
			rootNode = rootNode.getParentNode();
		}
		return rootNode;
	}
}
