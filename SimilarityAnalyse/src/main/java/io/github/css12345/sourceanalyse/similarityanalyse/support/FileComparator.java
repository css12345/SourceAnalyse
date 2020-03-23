package io.github.css12345.sourceanalyse.similarityanalyse.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;
import io.github.css12345.sourceanalyse.persistence.entity.Method;
import io.github.css12345.sourceanalyse.persistence.repository.MethodRepository;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;

@Component
public class FileComparator {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MethodComparator methodComparator;

	@Autowired
	private MethodRepository methodRepository;

	/**
	 * this method aim to find all {@link MethodCompare} and set it for argument
	 * fileCompare
	 * 
	 * @param fileCompare must set the compared fileInformations
	 */
	public void compare(FileCompare fileCompare) {
		FileInformation fileInformation1 = fileCompare.getFileInformation1();
		FileInformation fileInformation2 = fileCompare.getFileInformation2();

		if (logger.isInfoEnabled()) {
			logger.info("start to compare fileInformation1:{} and fileInformation2:{}",
					fileInformation1 == null ? null : fileInformation1.getFilePath(),
					fileInformation2 == null ? null : fileInformation2.getFilePath());
		}

		List<MethodCompare> methodCompares = new ArrayList<>();

		String version1 = fileInformation1 == null ? null : fileInformation1.getVersion();
		String version2 = fileInformation2 == null ? null : fileInformation2.getVersion();

		Set<String> briefMethodInformationSet = new TreeSet<>();
		if (fileInformation1 != null) {
			for (Method method : fileInformation1.getMethods())
				briefMethodInformationSet.add(method.getBriefMethodInformation());
		}
		if (fileInformation2 != null) {
			for (Method method : fileInformation2.getMethods())
				briefMethodInformationSet.add(method.getBriefMethodInformation());
		}
		for (String briefMethodInformation : briefMethodInformationSet)
			methodCompares.add(buildMethodCompare(briefMethodInformation, version1, version2));

		fileCompare.setMethodCompares(methodCompares);
		fileCompare.setState();

		if (logger.isInfoEnabled()) {
			logger.info("compare fileInformation1:{} and fileInformation2:{} finished, state is {}",
					fileInformation1 == null ? null : fileInformation1.getFilePath(),
					fileInformation2 == null ? null : fileInformation2.getFilePath(), fileCompare.getState());
		}
	}

	public MethodCompare buildMethodCompare(String briefMethodInformation, String version1, String version2) {
		List<Method> methods = methodRepository.findByBriefMethodInformation(briefMethodInformation);
		Method method1 = findMethod(methods, version1);
		Method method2 = findMethod(methods, version2);
		MethodCompare methodCompare = new MethodCompare(method1, method2);

		methodComparator.compare(methodCompare);
		return methodCompare;
	}

	private Method findMethod(List<Method> methods, String version) {
		for (Method method : methods) {
			if (method.getVersion().equals(version))
				return method;
		}
		return null;
	}
}
