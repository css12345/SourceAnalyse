package io.github.css12345.sourceanalyse.persistence.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.entity.ASTNodeInformation;
import io.github.css12345.sourceanalyse.jdtparse.entity.FileInformationDTO;
import io.github.css12345.sourceanalyse.jdtparse.entity.MethodInformationDTO;
import io.github.css12345.sourceanalyse.persistence.entity.ASTNode;
import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;
import io.github.css12345.sourceanalyse.persistence.entity.Method;
import io.github.css12345.sourceanalyse.persistence.support.FileInformationDTOMapper;

@Component
public class ConverterUtils {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FileInformationDTOMapper fileInformationDTOMapper;
	
	/**
	 * key is {@linkplain MethodInformationDTO#filePath
	 * filePath}-{@linkplain MethodInformationDTO#briefMethodInformation
	 * briefMethodInformation}, value is {@link Method}
	 */
	private Map<String, Method> methodCache = new HashMap<>();
	
	public void clearMethodCache() {
		methodCache.clear();
	}

	public FileInformation convert(String fileInformationDTOPath) {
		FileInformationDTO fileInformationDTO = getFileInformationDTO(fileInformationDTOPath);

		FileInformation fileInformation = new FileInformation(fileInformationDTO.getFilePath(),
				fileInformationDTO.getRootProjectPath(), fileInformationDTO.getVersion());
		for (MethodInformationDTO methodInformationDTO : fileInformationDTO.getHasMethods()) {
			Method method = convert(methodInformationDTO);
			fileInformation.addMethod(method);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("create file information for key {}", fileInformationDTOPath);
		}
		return fileInformation;
	}

	public Method convert(MethodInformationDTO methodInformationDTO) {
		String key = String.format("%s-%s", methodInformationDTO.getFilePath(),
				methodInformationDTO.getBriefMethodInformation());
		if (methodCache.containsKey(key)) {
			if (logger.isDebugEnabled()) {
				logger.debug("find method from method information cache for {}", key);
			}

			return methodCache.get(key);
		}

		Method method = new Method(methodInformationDTO.getFilePath(), methodInformationDTO.getVersion(),
				methodInformationDTO.getBriefMethodInformation());

		method.setRootNode(generateAstNodes(methodInformationDTO));

		methodCache.put(key, method);

		if (logger.isDebugEnabled()) {
			logger.debug("create method(no invocated methods), and save to cache for {}", key);
		}

		Collection<String> methodInvocations = methodInformationDTO.getMethodInvocationsMap().values();
		for (String methodInvocationInformation : methodInvocations) {
			final String JAVA_SUFFIX = ".java";
			int indexOfFirstSign = methodInvocationInformation.indexOf(JAVA_SUFFIX);
			String location = methodInvocationInformation.substring(0, indexOfFirstSign) + JAVA_SUFFIX;
			String briefMethodInformation = methodInvocationInformation
					.substring(indexOfFirstSign + JAVA_SUFFIX.length() + 1);

			FileInformationDTO methodInvocationFileInformationDTO = getFileInformationDTO(location);
			List<MethodInformationDTO> methodsOfFileInformationDTO = methodInvocationFileInformationDTO.getHasMethods();
			for (MethodInformationDTO methodOfFileInformationDTO : methodsOfFileInformationDTO) {
				if (methodOfFileInformationDTO.getBriefMethodInformation().equals(briefMethodInformation)) {
					method.invocate(convert(methodOfFileInformationDTO));
					break;
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("add invocated methods for method {}", key);
		}
		return method;
	}

	private FileInformationDTO getFileInformationDTO(String fileInformationDTOPath) {
		if (!new File(fileInformationDTOPath.replace(".java", ".json")).exists()) {
			throw new IllegalStateException(
					String.format("file %s didn't resolve and save to json file", fileInformationDTOPath));
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
