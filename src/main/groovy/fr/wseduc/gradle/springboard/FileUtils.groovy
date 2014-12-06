package fr.wseduc.gradle.springboard

import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project

class FileUtils {

	static def createFile(String propertiesFile, String templateFileName, String outputFileName) {
		def props = new Properties()
		props.load(new FileInputStream(new File(propertiesFile)))
		def bindings = [:]
		props.propertyNames().each{prop->
			bindings[prop]=props.getProperty(prop)
		}
		def engine = new SimpleTemplateEngine()
		def templateFile = new File(templateFileName)
		def output = engine.createTemplate(templateFile).make(bindings)
		def outputFile = new File(outputFileName)
		def parentFile = outputFile.getParentFile()
		if (parentFile != null)	parentFile.mkdirs()
		def fileWriter = new FileWriter(outputFile)
		fileWriter.write(output.toString())
		fileWriter.close()
	}


	static def copy(InputStream is, File output) {
		int read;
		byte[] bytes = new byte[1024];
		FileOutputStream out = new FileOutputStream(output)
		while ((read = is.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
	}

	static def createOrAppendProperties(File confProperties, String filename) {
		Boolean confExists = confProperties.exists()
		Map confPropertiesMap = [:]
		if (!confExists) {
			copy(FileUtils.class.getClassLoader().getResourceAsStream(filename),
					confProperties)
		} else {
			confProperties.eachLine {
				String[] l = it.split("=", 2)
				confPropertiesMap.put(l[0], l[1])
			}
			FileUtils.class.getClassLoader().getResourceAsStream(filename).eachLine {
				String[] l = it.split("=", 2)
				if (!confPropertiesMap.containsKey(l[0])) {
					confProperties.append(it + "\n")
				}
				confPropertiesMap.put(l[0], l[1])
			}
		}
		return confPropertiesMap
	}

	static def appendProperties(Project project, File file, confMap) {
		File f
		f = project.file(file.name)
		f.eachLine {
			String[] l = it.split("=", 2)
			if (!confMap.containsKey(l[0])) {
				f.append(it + "\n")
			}
			confMap.put(l[0], l[1])
		}
	}

}