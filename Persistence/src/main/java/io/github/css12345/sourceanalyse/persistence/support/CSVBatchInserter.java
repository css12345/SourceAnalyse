package io.github.css12345.sourceanalyse.persistence.support;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.css12345.sourceanalyse.jdtparse.Params;
import io.github.css12345.sourceanalyse.jdtparse.utils.CommandExecutor;
import io.github.css12345.sourceanalyse.persistence.entity.ASTNode;
import io.github.css12345.sourceanalyse.persistence.entity.FileInformation;
import io.github.css12345.sourceanalyse.persistence.entity.Method;

@Component
public class CSVBatchInserter implements InitializingBean {

	@Autowired
	private Params params;

	private String fileInformationFileName = "fileInformations.csv";

	private String methodFileName = "methods.csv";

	private String astNodeFileName = "astNodes.csv";

	private File baseDir;

	private File fileInformationFile;

	private File methodFile;

	private File astNodeFile;

	private final String[] FILEINFORMATION_HEADER = { "id", "filePath", "rootProjectPath", "version", "hasMethodIDs" };

	private final String[] METHOD_HEADER = { "id", "filePath", "version", "briefMethodInformation", "invocateMethodIDs",
			"astNodeID" };

	private final String[] ASTNODE_HEADER = { "id", "type", "childASTNodeIDs" };

	private boolean fileInformationHeaderAdded = false;

	private boolean methodHeaderAdded = false;

	private boolean astNodeHeaderAdded = false;

	public File getBaseDir() {
		return baseDir;
	}

	public void write(List<FileInformation> fileInformations) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileInformationFile, true));
			CSVFormat format = fileInformationHeaderAdded ? CSVFormat.DEFAULT.withEscape('\\')
					: CSVFormat.DEFAULT.withEscape('\\').withHeader(FILEINFORMATION_HEADER);
			if (!fileInformationHeaderAdded)
				fileInformationHeaderAdded = true;
			try (CSVPrinter printer = new CSVPrinter(out, format)) {
				for (FileInformation fileInformation : fileInformations) {
					Object[] values = new Object[FILEINFORMATION_HEADER.length];
					values[0] = fileInformation.getId();
					values[1] = fileInformation.getFilePath();
					values[2] = fileInformation.getRootProjectPath();
					values[3] = fileInformation.getVersion();

					StringBuilder builder = new StringBuilder();
					List<Method> methods = fileInformation.getMethods();
					for (int j = 0; j < methods.size(); j++) {
						builder.append(methods.get(j).getId());
						if (j < methods.size() - 1)
							builder.append(',');
					}
					values[4] = builder.toString();

					printer.printRecord(values);

					writeMethod(methods);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeMethod(List<Method> methods) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(methodFile, true));
			CSVFormat format = methodHeaderAdded ? CSVFormat.DEFAULT.withEscape('\\')
					: CSVFormat.DEFAULT.withEscape('\\').withHeader(METHOD_HEADER);
			if (!methodHeaderAdded)
				methodHeaderAdded = true;
			try (CSVPrinter printer = new CSVPrinter(out, format)) {
				for (Method method : methods) {
					Object[] values = new Object[METHOD_HEADER.length];
					values[0] = method.getId();
					values[1] = method.getFilePath();
					values[2] = method.getVersion();
					values[3] = method.getBriefMethodInformation();
					values[5] = method.getRootNode().getId();
					List<Method> methodInvocations = method.getMethodInvocations();
					Set<String> idOfMethodInvocationSet = methodInvocations.stream()
							.map(methodInvocation -> methodInvocation.getId()).collect(Collectors.toSet());
					StringBuilder builder = new StringBuilder();
					for (Iterator<String> iterator = idOfMethodInvocationSet.iterator(); iterator.hasNext();) {
						builder.append(iterator.next());
						if (iterator.hasNext())
							builder.append(',');
					}
					values[4] = builder.toString();
					printer.printRecord(values);

					writeASTNode(new ArrayList<>(Arrays.asList(method.getRootNode())));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeASTNode(List<ASTNode> astNodes) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(astNodeFile, true));
			CSVFormat format = astNodeHeaderAdded ? CSVFormat.DEFAULT.withEscape('\\')
					: CSVFormat.DEFAULT.withEscape('\\').withHeader(ASTNODE_HEADER);
			if (!astNodeHeaderAdded)
				astNodeHeaderAdded = true;
			try (CSVPrinter printer = new CSVPrinter(out, format)) {
				for (ASTNode astNode : astNodes) {
					Object[] values = new Object[ASTNODE_HEADER.length];
					values[0] = astNode.getId();
					values[1] = astNode.getType();
					List<ASTNode> childASTNodes = astNode.getChilds();
					StringBuilder builder = new StringBuilder();
					for (int i = 0; i < childASTNodes.size(); i++) {
						builder.append(childASTNodes.get(i).getId());
						if (i < childASTNodes.size() - 1)
							builder.append(',');
					}
					values[2] = builder.toString();
					printer.printRecord(values);

					writeASTNode(childASTNodes);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void executeCSVImport() {
		try {
			File tempScriptFile = File.createTempFile("data-import", ".cypher");
			StringBuilder builder = new StringBuilder();
			builder.append("CREATE INDEX ON :FileInformation(id);\n");
			builder.append("CREATE INDEX ON :FileInformation(filePath);\n");
			builder.append("CREATE INDEX ON :Method(id);\n");
			builder.append("CREATE INDEX ON :Method(briefMethodInformation, version);\n");
			builder.append("CREATE INDEX ON :ASTNode(id);\n\n");
			FileUtils.writeStringToFile(tempScriptFile, builder.toString(), "UTF-8", true);

			builder = new StringBuilder();
			builder.append("USING PERIODIC COMMIT 500\n");
			builder.append(String.format("LOAD CSV WITH HEADERS FROM 'file:///%s' AS row\n",
					fileInformationFile.getAbsolutePath().replace("\\", "/")));
			builder.append(
					"CREATE (fileInformation:FileInformation {id: row.id, filePath: row.filePath, rootProjectPath: row.rootProjectPath, version: row.version});\n\n");
			FileUtils.writeStringToFile(tempScriptFile, builder.toString(), "UTF-8", true);

			builder = new StringBuilder();
			builder.append("USING PERIODIC COMMIT 500\n");
			builder.append(
					String.format("LOAD CSV WITH HEADERS FROM 'file:///%s' AS row\n", methodFile.getAbsolutePath().replace("\\", "/")));
			builder.append(
					"CREATE (method:Method {id: row.id, filePath: row.filePath, version: row.version, briefMethodInformation: row.briefMethodInformation});\n\n");
			FileUtils.writeStringToFile(tempScriptFile, builder.toString(), "UTF-8", true);

			builder = new StringBuilder();
			builder.append("USING PERIODIC COMMIT 500\n");
			builder.append(
					String.format("LOAD CSV WITH HEADERS FROM 'file:///%s' AS row\n", astNodeFile.getAbsolutePath().replace("\\", "/")));
			builder.append("CREATE (astNode:ASTNode {id: row.id, type: row.type});\n\n");
			FileUtils.writeStringToFile(tempScriptFile, builder.toString(), "UTF-8", true);

			builder = new StringBuilder();
			builder.append("USING PERIODIC COMMIT 500\n");
			builder.append(String.format("LOAD CSV WITH HEADERS FROM 'file:///%s' AS row\n",
					fileInformationFile.getAbsolutePath().replace("\\", "/")));
			builder.append("MATCH (fileInformation:FileInformation {id: row.id})\n");
			builder.append("UNWIND split(row.hasMethodIDs, ',') AS hasMethodID\n");
			builder.append("MATCH (method:Method {id: hasMethodID})\n");
			builder.append("CREATE (fileInformation)-[:HASMETHODS]->(method);\n\n");
			FileUtils.writeStringToFile(tempScriptFile, builder.toString(), "UTF-8", true);

			builder = new StringBuilder();
			builder.append("USING PERIODIC COMMIT 500\n");
			builder.append(String.format("LOAD CSV WITH HEADERS FROM 'file:///%s' AS row\n",
					methodFile.getAbsolutePath().replace("\\", "/")));
			builder.append("MATCH (callerMethod:Method {id: row.id})\n");
			builder.append("UNWIND split(row.invocateMethodIDs, ',') AS invocateMethodID\n");
			builder.append("MATCH (calleeMethod:Method {id: invocateMethodID})\n");
			builder.append("CREATE (callerMethod)-[:INVOCATE]->(calleeMethod);\n\n");
			FileUtils.writeStringToFile(tempScriptFile, builder.toString(), "UTF-8", true);
			
			builder = new StringBuilder();
			builder.append("USING PERIODIC COMMIT 500\n");
			builder.append(String.format("LOAD CSV WITH HEADERS FROM 'file:///%s' AS row\n",
					methodFile.getAbsolutePath().replace("\\", "/")));
			builder.append("MATCH (method:Method {id: row.id})\n");
			builder.append("MATCH (astNode:ASTNode {id: row.astNodeID})\n");
			builder.append("CREATE (method)-[:CONTAIN]->(astNode);\n\n");
			FileUtils.writeStringToFile(tempScriptFile, builder.toString(), "UTF-8", true);
			
			builder = new StringBuilder();
			builder.append("USING PERIODIC COMMIT 500\n");
			builder.append(String.format("LOAD CSV WITH HEADERS FROM 'file:///%s' AS row\n",
					astNodeFile.getAbsolutePath().replace("\\", "/")));
			builder.append("MATCH (parentASTNode:ASTNode {id: row.id})\n");
			builder.append("UNWIND split(row.childASTNodeIDs, ',') AS childASTNodeID\n");
			builder.append("MATCH (childASTNode:ASTNode {id: childASTNodeID})\n");
			builder.append("CREATE (parentASTNode)-[:CHILD]->(childASTNode);\n\n");
			FileUtils.writeStringToFile(tempScriptFile, builder.toString(), "UTF-8", true);
			
			String executeCommand = String.format("cd %s & %s %s | cypher-shell",
					CommandExecutor.isWindows ? "/d %NEO4J_HOME%\\bin" : "$NEO4J_HOME/bin",
					CommandExecutor.isWindows ? "type" : "cat", tempScriptFile.getAbsolutePath());
			CommandExecutor.execute(executeCommand);
			
			tempScriptFile.delete();
			FileUtils.deleteDirectory(baseDir);
			createNotExistFilesAndWriteHeaders();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		baseDir = new File(params.getSITE_DATA_DIR(), "neo4j");

		fileInformationFile = new File(baseDir, fileInformationFileName);
		methodFile = new File(baseDir, methodFileName);
		astNodeFile = new File(baseDir, astNodeFileName);

		createNotExistFilesAndWriteHeaders();
	}

	private void createNotExistFilesAndWriteHeaders() {
		if (!baseDir.exists())
			baseDir.mkdirs();
		try {
			if (fileInformationFile.createNewFile()) {
				fileInformationHeaderAdded = false;
				write(Collections.emptyList());
			} else {
				fileInformationHeaderAdded = true;
			}
			if (methodFile.createNewFile()) {
				methodHeaderAdded = false;
				write(Collections.emptyList());
			} else {
				methodHeaderAdded = true;
			}
			if (astNodeFile.createNewFile()) {
				astNodeHeaderAdded = false;
				writeASTNode(Collections.emptyList());
			} else {
				astNodeHeaderAdded = true;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
