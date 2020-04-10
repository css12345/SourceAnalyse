package io.github.css12345.sourceanalyse.similarityanalyse.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CacheUtils {
	public static <T> List<T> readAllFromCacheFile(File cacheFile, Class<T> clazz) {
		try {
			List<String> lines = FileUtils.readLines(cacheFile, "UTF-8");
			List<T> list = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
			for (String line : lines) {
				T t = mapper.readValue(line, clazz);
				list.add(t);
			}
			return list;
		} catch (IOException e) {
			String errorMessage = String.format("when read type %s from file %s occur an IOException", clazz.getTypeName(), cacheFile.getAbsolutePath());
			throw new RuntimeException(errorMessage, e);
		}
	}
	
	public static void saveToCacheFile(File cacheFile, Object value, boolean append) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String valueInJsonString = mapper.writeValueAsString(value) + "\n";
			FileUtils.write(cacheFile, valueInJsonString, "UTF-8", append);
		} catch (IOException e) {
			String errorMessage = String.format("when write value %s to file %s occur an IOException", value, cacheFile.getAbsolutePath());
			throw new RuntimeException(errorMessage, e);
		}
	}
}
