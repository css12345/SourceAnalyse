package io.github.css12345.sourceanalyse.display.utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import io.github.css12345.sourceanalyse.similarityanalyse.utils.CacheUtils;

public class ProjectIDUtils {

	private static final String PROJECT_ID_CACHE_FILEPATH = "cache/idOfProjects.json";

	private static Map<String, String> projectPathAndIDMap = new HashMap<>();

	static {
		File cacheFile = new File(System.getProperty("user.dir"), PROJECT_ID_CACHE_FILEPATH);
		List<Map> readAllFromCacheFile = CacheUtils.readAllFromCacheFile(cacheFile, Map.class);
		if (!readAllFromCacheFile.isEmpty())
			projectPathAndIDMap = readAllFromCacheFile.get(0);
	}

	public static String generateID(String projectPath) {
		String id = UUID.randomUUID().toString();
		while(idGenerated(id))
			id = UUID.randomUUID().toString();
		
		return id;
	}
	
	public static String getProjectPathById(String id) {
		for (Entry<String, String> entry : projectPathAndIDMap.entrySet()) {
			if (entry.getValue().equals(id))
				return entry.getKey();
		}
		return null;
	}
	
	public static void saveToCacheFile() {
		File cacheFile = new File(System.getProperty("user.dir"), PROJECT_ID_CACHE_FILEPATH);
		CacheUtils.saveToCacheFile(cacheFile, projectPathAndIDMap, false);
	}
	
	public static String getIDByProjectPath(String projectPath) {
		return projectPathAndIDMap.get(projectPath);
	}
	
	public static void remove(String projectPath) {
		projectPathAndIDMap.remove(projectPath);
	}

	private static boolean idGenerated(String id) {
		for (String value : projectPathAndIDMap.values()) {
			if (value.equals(id))
				return true;
		}
		return false;
	}

	public static boolean checkPathAdded(String projectPath) {
		return projectPathAndIDMap.containsKey(projectPath);
	}

	public static String generateAndSaveID(String projectPath) {
		String id = generateID(projectPath);
		projectPathAndIDMap.put(projectPath, id);
		saveToCacheFile();
		return id;
	}
}
