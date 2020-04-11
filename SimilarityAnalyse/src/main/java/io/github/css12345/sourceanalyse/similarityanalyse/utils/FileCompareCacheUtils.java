package io.github.css12345.sourceanalyse.similarityanalyse.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.similarityanalyse.entity.FileCompare;

@Component
public class FileCompareCacheUtils implements InitializingBean {
	
	@Autowired
	private Params params;

	private static final String FILE_COMPARES_FILEPATH = "cache/fileCompares.json";

	private static Map<Pair<String, String>, FileCompare> fileCompareCache = new HashMap<>();
	
	private static File cacheFile;

	public void loadFileCompareCache() {
		cacheFile = new File(params.getSITE_DATA_DIR(), FILE_COMPARES_FILEPATH);
		File cacheDirectory = cacheFile.getParentFile();
		if (!cacheDirectory.exists())
			cacheDirectory.mkdirs();
		try {
			cacheFile.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		List<FileCompare> fileCompares = CacheUtils.readAllFromCacheFile(cacheFile, FileCompare.class);
		for (FileCompare fileCompare : fileCompares) {
			fileCompareCache.put(Pair.of(fileCompare.getFilePath1(), fileCompare.getFilePath2()), fileCompare);
		}
	}
	
	public static FileCompare getFileCompare(String filePath1, String filePath2) {
		return fileCompareCache.get(Pair.of(filePath1, filePath2));
	}
	
	public static boolean contains(String filePath1, String filePath2) {
		return fileCompareCache.containsKey(Pair.of(filePath1, filePath2));
	}
	
	public static void saveToCacheFile(FileCompare savedFileCompare) {
		boolean contained = contains(savedFileCompare.getFilePath1(), savedFileCompare.getFilePath2());
		fileCompareCache.put(Pair.of(savedFileCompare.getFilePath1(), savedFileCompare.getFilePath2()), savedFileCompare);
		
		if (!contained) {
			CacheUtils.saveToCacheFile(cacheFile, savedFileCompare, true);
		} else {
			try {
				FileUtils.writeStringToFile(cacheFile, "", "UTF-8");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			for (FileCompare fileCompare : fileCompareCache.values()) {
				CacheUtils.saveToCacheFile(cacheFile, fileCompare, true);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		loadFileCompareCache();
	}
}
