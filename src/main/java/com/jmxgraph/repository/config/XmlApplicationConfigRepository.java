package com.jmxgraph.repository.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.jmxgraph.domain.ApplicationConfig;

public class XmlApplicationConfigRepository implements ApplicationConfigRepository {
	
	private static final String XML_REPO_LOCATION = System.getProperty("user.home") + "/.jmxgraph/config.xml";
	
	private JAXBContext jaxbContext = null;
	
	private XmlApplicationConfigRepository() {
		try {
			jaxbContext = JAXBContext.newInstance(ApplicationConfig.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// http://stackoverflow.com/questions/11165852/java-singleton-and-synchronization
	private static class InstanceHolder {
		private static final XmlApplicationConfigRepository instance = new XmlApplicationConfigRepository();
	}
	
	public static XmlApplicationConfigRepository getInstance() {
		return InstanceHolder.instance;
	}

	@Override
	public ApplicationConfig getApplicationConfig() {
		if (Files.exists(Paths.get(XML_REPO_LOCATION))) {
			return JAXB.unmarshal(new File(XML_REPO_LOCATION), ApplicationConfig.class);
		}
		
		return new ApplicationConfig();
	}

	@Override
	public void saveApplicationConfig(ApplicationConfig applicationConfig) throws Exception {
		StringWriter stringWriter = new StringWriter();
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		 
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(applicationConfig, stringWriter);

		Path filePath = Paths.get(XML_REPO_LOCATION);
		Files.deleteIfExists(filePath);
		Files.copy(new ByteArrayInputStream(stringWriter.toString().getBytes()), filePath);
	}
}
