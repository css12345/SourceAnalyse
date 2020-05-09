package io.github.css12345.sourceanalyse.similarityanalyse.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.MethodCompare;

@Component
public class FileCompareCacheUtils implements InitializingBean {

	@Autowired
	private Params params;

	private static final String FILE_COMPARES_DIRECTORY_PATH = "cache/fileCompares";

	private static Map<Pair<String, String>, FileCompare> fileCompareCache = new HashMap<>();

	private static Map<Pair<Pair<String, String>, Pair<String, String>>, MethodCompare> methodCompareCache = new HashMap<>();

	private static File cacheDirectory;

	private static Map<String, Boolean> fileComparesLoadedMap = new HashMap<>();

	public static void loadFileCompares(File cacheFile) {
		String filePath = cacheFile.getAbsolutePath();
		if (!fileComparesLoadedMap.containsKey(filePath))
			throw new IllegalArgumentException(
					String.format("cache file %s didn't in fileComparesLoadedMap", filePath));

		boolean loaded = fileComparesLoadedMap.get(filePath);
		if (loaded)
			return;

		List<FileCompare> fileCompares = CacheUtils.readAllFromCacheFile(cacheFile, FileCompare.class);
		for (FileCompare fileCompare : fileCompares) {
			load(fileCompare);
		}
		fileComparesLoadedMap.put(filePath, true);
	}

	public static void load(FileCompare fileCompare) {
		fileCompareCache.put(Pair.of(fileCompare.getFilePath1(), fileCompare.getFilePath2()), fileCompare);
		for (MethodCompare methodCompare : fileCompare.getMethodCompares()) {
			methodCompareCache.put(
					Pair.of(Pair.of(methodCompare.getBriefMethodInformation1(), methodCompare.getVersion1()),
							Pair.of(methodCompare.getBriefMethodInformation2(), methodCompare.getVersion2())),
					methodCompare);
		}
	}

	public static FileCompare getFileCompare(String filePath1, String filePath2, String relatedFileName) {
		File relatedCompareFile = new File(cacheDirectory, relatedFileName);
		if (relatedCompareFile.exists()) {
			if (!fileComparesLoadedMap.containsKey(relatedCompareFile.getAbsolutePath()) || !fileComparesLoadedMap.get(relatedCompareFile.getAbsolutePath())) {
				if (!fileComparesLoadedMap.containsKey(relatedCompareFile.getAbsolutePath()))
					fileComparesLoadedMap.put(relatedCompareFile.getAbsolutePath(), false);
				loadFileCompares(relatedCompareFile);
			}
		}
		return fileCompareCache.get(Pair.of(filePath1, filePath2));
	}

	public static boolean contains(String filePath1, String filePath2, String relatedFileName) {
		File relatedCompareFile = new File(cacheDirectory, relatedFileName);
		if (relatedCompareFile.exists()) {
			if (!fileComparesLoadedMap.containsKey(relatedCompareFile.getAbsolutePath()) || !fileComparesLoadedMap.get(relatedCompareFile.getAbsolutePath())) {
				if (!fileComparesLoadedMap.containsKey(relatedCompareFile.getAbsolutePath()))
					fileComparesLoadedMap.put(relatedCompareFile.getAbsolutePath(), false);
				loadFileCompares(relatedCompareFile);
			}
		}
		return fileCompareCache.containsKey(Pair.of(filePath1, filePath2));
	}

	public static MethodCompare getMethodCompare(String briefMethodInformation1, String version1,
			String briefMethodInformation2, String version2) {
		Pair<Pair<String, String>, Pair<String, String>> key = Pair.of(Pair.of(briefMethodInformation1, version1),
				Pair.of(briefMethodInformation2, version2));
		return methodCompareCache.get(key);
	}

	public static boolean contains(String briefMethodInformation1, String version1, String briefMethodInformation2,
			String version2) {
		Pair<Pair<String, String>, Pair<String, String>> key = Pair.of(Pair.of(briefMethodInformation1, version1),
				Pair.of(briefMethodInformation2, version2));
		return methodCompareCache.containsKey(key);
	}

	public static void saveToCacheFile(FileCompare savedFileCompare, String relatedFileName) {
		File relatedCompareFile = new File(cacheDirectory, relatedFileName);
		try {
			boolean firstCreate = relatedCompareFile.createNewFile();

			load(savedFileCompare);
			CacheUtils.saveToCacheFile(relatedCompareFile, savedFileCompare, true);

			if (firstCreate) {
				fileComparesLoadedMap.put(relatedCompareFile.getAbsolutePath(), true);
			}
		} catch (IOException e) {
			throw new RuntimeException(
					String.format("when save file compare to file %s occur an IOException", relatedCompareFile), e);
		}

	}

	public static void deleteFileComparesForProject(String projectId) {
		File[] relatedCompareFiles = cacheDirectory
				.listFiles((dir, name) -> name.contains(projectId));
		for (File relatedCompareFile : relatedCompareFiles) {
			deleteFileCompare(relatedCompareFile);
		}
	}

	private static void deleteFileCompare(File relatedCompareFile) {
		if (fileComparesLoadedMap.containsKey(relatedCompareFile.getAbsolutePath())) {
			boolean loaded = fileComparesLoadedMap.get(relatedCompareFile.getAbsolutePath());
			if (loaded) {
				List<FileCompare> fileCompares = CacheUtils.readAllFromCacheFile(relatedCompareFile, FileCompare.class);
				for (FileCompare fileCompare : fileCompares) {
					remove(fileCompare);
				}
			}

			relatedCompareFile.delete();
			fileComparesLoadedMap.remove(relatedCompareFile.getAbsolutePath());
		}
	}

	private static void remove(FileCompare fileCompare) {
		fileCompareCache.remove(Pair.of(fileCompare.getFilePath1(), fileCompare.getFilePath2()), fileCompare);
		for (MethodCompare methodCompare : fileCompare.getMethodCompares()) {
			methodCompareCache.remove(
					Pair.of(Pair.of(methodCompare.getBriefMethodInformation1(), methodCompare.getVersion1()),
							Pair.of(methodCompare.getBriefMethodInformation2(), methodCompare.getVersion2())),
					methodCompare);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		cacheDirectory = new File(params.getSITE_DATA_DIR(), FILE_COMPARES_DIRECTORY_PATH);
		if (!cacheDirectory.exists())
			cacheDirectory.mkdirs();
		for (File comparedFile : cacheDirectory.listFiles()) {
			fileComparesLoadedMap.put(comparedFile.getAbsolutePath(), false);
		}
	}
}
