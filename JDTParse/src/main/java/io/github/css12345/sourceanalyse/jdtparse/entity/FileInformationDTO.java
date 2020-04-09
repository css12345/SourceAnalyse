package io.github.css12345.sourceanalyse.jdtparse.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileInformationDTO {
	private String filePath;
	
	private String rootProjectPath;

	private String version;

	private List<MethodInformationDTO> hasMethods = new ArrayList<>();
	
	public FileInformationDTO() {
		
	}
	
	public FileInformationDTO(FileInformation fileInformation, Map<String, String> classQualifiedNameLocationMap) {
		this.filePath = fileInformation.getFile().getAbsolutePath();
		this.rootProjectPath = fileInformation.getRootProjectPath();
		this.version = new File(rootProjectPath).getParentFile().getName();
		
		for (MethodInformation methodInformation : fileInformation.getMethodInformations()) {
			MethodInformationDTO methodInformationDTO = new MethodInformationDTO(methodInformation, this, classQualifiedNameLocationMap);
			hasMethods.add(methodInformationDTO);
		}
	}

	public String getRootProjectPath() {
		return rootProjectPath;
	}

	public void setRootProjectPath(String rootProjectPath) {
		this.rootProjectPath = rootProjectPath;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getVersion() {
		return version;
	}

	public List<MethodInformationDTO> getHasMethods() {
		return hasMethods;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setHasMethods(List<MethodInformationDTO> hasMethods) {
		this.hasMethods = hasMethods;
	}


	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("filePath:" + filePath + "\n");
		stringBuilder.append("rootProjectPath:" + rootProjectPath + "\n");
		stringBuilder.append("version:" + version + "\n");
		stringBuilder.append("hasMethods:");
		for (int i = 0; i < hasMethods.size(); i++) {
			stringBuilder.append(hasMethods.get(i).getBriefMethodInformation());
			if (i != hasMethods.size() - 1)
				stringBuilder.append(',');
			else
				stringBuilder.append('\n');
		}
		
		stringBuilder.append('\n');
		for (MethodInformationDTO methodInformationDTO : hasMethods)
			stringBuilder.append(methodInformationDTO);
		stringBuilder.append("________________________________________________________\n");
		return stringBuilder.toString();
	}

}
