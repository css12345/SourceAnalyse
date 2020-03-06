package io.github.css12345.sourceanalyse.jdtparse.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.css12345.sourceanalyse.jdtparse.exception.CommandExecuteException;

public class CommandExecutor {
	
	private static Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

	public static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	public static String execute(String command) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("execute commond is {}", command);
			}
			
			Process process;
			String charset;
			if (isWindows) {
				process = Runtime.getRuntime().exec("cmd.exe /c " + command);
				charset = "GBK";
			} else {
				process = Runtime.getRuntime().exec("sh -c " + command);
				charset = "UTF-8";
			}

			StringBuilder result = new StringBuilder();
			InputStream inputStream = process.getInputStream();
			InputStream errorStream = process.getErrorStream();
			Consumer<String> consumer = str -> {
				result.append(str);
				result.append('\n');
			};
			new BufferedReader(new InputStreamReader(inputStream, charset)).lines().forEach(consumer);
			new BufferedReader(new InputStreamReader(errorStream, charset)).lines().forEach(consumer);

			int exitCode = process.waitFor();
			if (exitCode != 0) {
				String message = "the command " + command + " 's exit code is " + exitCode
						+ ", is not a normal terminate.\nspecific error is\n" + result.toString();
				throw new CommandExecuteException(message);
			}
			
			if (logger.isInfoEnabled()) {
				logger.info("execute result is {}", result.toString());
			}
			return result.toString();
		} catch (IOException | InterruptedException e) {
			String message = "an exception " + e + " occur when execute command " + command;
			throw new CommandExecuteException(message, e);
		}
	}

	public static String execute(List<String> commands) {
		String suffix = isWindows ? ".bat" : ".sh";
		try {
			File tempFile = File.createTempFile("tmp", suffix);
			FileUtils.writeLines(tempFile, commands);
			return execute(tempFile);
		} catch (IOException e) {
			String message = "an exception " + e + " occur when execute commands " + commands;
			throw new CommandExecuteException(message, e);
		}
	}

	public static String execute(File scriptFile) {
		return execute(scriptFile.getAbsolutePath());
	}

}
