package io.github.css12345.sourceanalyse.jdtparse.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

@Disabled
public class TestExecuteCommand {
	
	@Test
	public void testExecuteOneCommand() throws IOException {
//		//String command = "cd /d D:\\Users\\lcs\\workspace\\readinglist & mvn dependency:list -DoutputFile=result.txt";
//		String command = "cd /d D:\\Users\\cs\\Documents\\GitHub\\oim-fx\\oim-client-parent & mvn help:evaluate -Dexpression=project.modules -q -DforceStdout";
//		//String command = "cd /d D:\\Users\\lenovo\\workspace\\Fruit-Sales-Platform & mvn help:evaluate -Dexpression=project.modules -q -DforceStdout";
//		String result = CommandExecutor.execute(command);
//		String[] lines = result.split("\n");
//		for (String line : lines) {
//			line = line.trim();
//			String prefix = "<string>";
//			String suffix = "</string>";
//			if (line.startsWith(prefix) && line.endsWith(suffix)) {
//				//System.out.println(line);
//				String modulePath = line.substring(line.indexOf(prefix) + prefix.length(), line.lastIndexOf(suffix));
//				//System.out.println(modulePath);
//				File moduleFile = new File("D:\\Users\\cs\\Documents\\GitHub\\oim-fx\\oim-client-parent", modulePath);
//				System.out.println(moduleFile.getAbsolutePath());
//				System.out.println(moduleFile.getCanonicalPath());
//			}
//		}
//		//System.out.println(result);
		
		String command = "cd /d D:\\Users\\cs\\Documents\\javaSourceCode\\druid-3cd92eca5faa5d584c6341111fccba7663213ecc & mvn dependency:list -DoutputFile=result.txt -DoutputAbsoluteArtifactFilename=true";
		String result = CommandExecutor.execute(command);
		System.out.println(result);
	}

	@Test
	public void testExecuteScriptFile() throws IOException {
		File scriptFile = new ClassPathResource("test.bat").getFile();
		String result = CommandExecutor.execute(scriptFile);
		System.out.println(result);
	}

	@Test
	public void testExecuteMultiCommands() {
		List<String> commands = new ArrayList<>(Arrays.asList("cd /d D:\\Users\\lcs\\workspace\\readinglist",
				"mvn dependency:list -DoutputFile=result.txt"));
		String result = CommandExecutor.execute(commands);
		System.out.println(result);
	}
}
