package io.github.css12345.sourceanalyse.similarityanalyse.support;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.persistence.entity.Method;
import io.github.css12345.sourceanalyse.persistence.repository.MethodRepository;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.utils.FileCompareCacheUtils;

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
	 * @param version1
	 * @param version2
	 * @param relatedFileName 
	 */
	public void compare(FileCompare fileCompare, String version1, String version2, String relatedFileName) {
		String filePath1 = fileCompare.getFilePath1();
		String filePath2 = fileCompare.getFilePath2();

		if (FileCompareCacheUtils.contains(filePath1, filePath2, relatedFileName)) {
			FileCompare cachedFileCompare = FileCompareCacheUtils.getFileCompare(filePath1, filePath2, relatedFileName);
			fileCompare.setMethodCompares(cachedFileCompare.getMethodCompares());
			fileCompare.setState(cachedFileCompare.getState());

			if (logger.isDebugEnabled()) {
				logger.debug("find cached fileCompare for filePath1:{} and filePath2:{}", filePath1, filePath2);
			}
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("start to compare filePath1:{} and filePath2:{}", filePath1, filePath2);
		}

		List<Method> methods1 = methodRepository.findByFilePath(filePath1);
		List<Method> methods2 = methodRepository.findByFilePath(filePath2);

		Set<String> briefMethodInformationSet = new TreeSet<>();

		for (Method method : methods1)
			briefMethodInformationSet.add(method.getBriefMethodInformation());

		for (Method method : methods2)
			briefMethodInformationSet.add(method.getBriefMethodInformation());

		for (String briefMethodInformation : briefMethodInformationSet) {
			MethodCompare methodCompare = buildMethodCompare(briefMethodInformation, version1, version2);
			if (!fileCompare.getMethodCompares().contains(methodCompare)) {
				methodComparator.compare(methodCompare);
				fileCompare.getMethodCompares().add(methodCompare);
			}
		}
		fileCompare.judgeAndSetState();

		if (logger.isDebugEnabled()) {
			logger.debug("compare filePath1:{} and filePath2:{} finished, state is {}", filePath1, filePath2,
					fileCompare.getState());
		}

		FileCompareCacheUtils.saveToCacheFile(fileCompare, relatedFileName);
		if (logger.isDebugEnabled()) {
			logger.debug("add fileCompare for filePath1:{} and filePath2:{} to cache file finished", filePath1,
					filePath2);
		}
	}

	public MethodCompare buildMethodCompare(String briefMethodInformation, String version1, String version2) {
		Method method1 = methodRepository.findByBriefMethodInformationAndVersion(briefMethodInformation, version1);
		Method method2 = methodRepository.findByBriefMethodInformationAndVersion(briefMethodInformation, version2);
		MethodCompare methodCompare = new MethodCompare(method1 == null ? null : method1.getBriefMethodInformation(),
				version1, method2 == null ? null : method2.getBriefMethodInformation(), version2);

		return methodCompare;
	}
}
