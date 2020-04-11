package io.github.css12345.sourceanalyse.display.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.css12345.sourceanalyse.display.dao.DifferenceDao;
import io.github.css12345.sourceanalyse.display.utils.ProjectIDUtils;
import io.github.css12345.sourceanalyse.jdtparse.entity.Project;

@SpringBootTest
public class TestDifferenceController {
	@Autowired
	private DifferenceDao differenceDao;

	@Autowired
	private DifferenceController differenceController;

	@Test
	public void testCompareFiles() {
		Project project1 = differenceController
				.getProjectById(ProjectIDUtils.getIDByProjectPath("D:\\tmp\\fastjson\\1.1.44\\fastjson-1.1.44"));
		Project project2 = differenceController
				.getProjectById(ProjectIDUtils.getIDByProjectPath("D:\\tmp\\fastjson\\1.1.157\\fastjson-1.1.157"));
		List<String> filePaths = new ArrayList<>(
				Arrays.asList("\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSON.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONArray.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONAware.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONException.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONObject.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONPObject.java",
						"\\fastjson-1.1.44\\src\\main\\java\\com\\alibaba\\fastjson\\JSONReader.java",
						"\\fastjson-1.1.157\\src\\main\\java\\com\\alibaba\\fastjson\\JSONStreamAware.java",
						"\\fastjson-1.1.157\\src\\main\\java\\com\\alibaba\\fastjson\\JSONStreamContext.java",
						"\\fastjson-1.1.157\\src\\main\\java\\com\\alibaba\\fastjson\\JSONWriter.java",
						"\\fastjson-1.1.157\\src\\main\\java\\com\\alibaba\\fastjson\\PropertyNamingStrategy.java",
						"\\fastjson-1.1.157\\src\\main\\java\\com\\alibaba\\fastjson\\TypeReference.java"));
		String prefix1 = "D:\\tmp\\fastjson\\1.1.44";
		String prefix2 = "D:\\tmp\\fastjson\\1.1.157";
		for (int i = 0; i < filePaths.size(); i++) {
			String filePath = filePaths.get(i);
			if (filePath.contains("1.1.44"))
				filePath = prefix1 + filePath;
			else
				filePath = prefix2 + filePath;

			filePaths.set(i, filePath);
		}

		differenceDao.compareFiles(project1, project2, filePaths);
	}
}
