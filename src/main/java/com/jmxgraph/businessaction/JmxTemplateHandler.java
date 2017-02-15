package com.jmxgraph.businessaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.xml.bind.JAXB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.domain.defaults.DefaultObject;
import com.jmxgraph.domain.jmx.JmxAttribute;
import com.jmxgraph.domain.jmx.JmxGraph;
import com.jmxgraph.domain.jmx.JmxObjectName;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.mbean.template.GraphTemplate;
import com.jmxgraph.mbean.template.JmxTemplate;
import com.jmxgraph.mbean.template.MBeanTemplate;
import com.jmxgraph.repository.jmx.JdbcAttributeRepository;
import com.jmxgraph.repository.jmx.JmxAttributeRepository;

public class JmxTemplateHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(JmxTemplateHandler.class);
	
	private static final String TEMPLATE_LOCATION = System.getProperty("user.home") + "/.jmxgraph/template/";

	private JmxTemplateHandler() {  }
	
	private static class InstanceHolder {
		private static final JmxTemplateHandler instance = new JmxTemplateHandler();
	}
	
	public static JmxTemplateHandler getInstance() {
		return InstanceHolder.instance;
	}
	
	public void processDefaultJmxTemplates() throws IOException {
		for (DefaultObject defaultObject : DefaultObject.values()) {
			// TODO: Logic to disable the default object
			try (InputStream inputStream = defaultObject.getTemplateFile()) {
				processJmxTemplate(inputStream);
			}
		}
	}
	
	public void processJmxTemplates(String... templateFilePaths) {
		for (String filePath : templateFilePaths) {
			try {
				processJmxTemplate(new FileInputStream(new File(filePath)));
			} catch (Exception e) {
				logger.error("Error while processing template file: " + filePath, e);
			}
		}
	}
	
	public void saveJmxTemplates(String... templateFilePaths) {
		for (String filePath : templateFilePaths) {
			try {
				File templateFile = new File(filePath);
				Path destination = Paths.get(TEMPLATE_LOCATION + templateFile.getName());
				if (destination.toFile().exists()) {
					logger.warn("File: " + filePath + " already exists in the jmx-graph working directory.");
				} else {
					logger.debug("Copying template file: " + filePath + " to jmx-graph working directory.");
					if (!Files.exists(Paths.get(TEMPLATE_LOCATION))) {
						Files.createDirectories(Paths.get(TEMPLATE_LOCATION));
					}
					
					Files.copy(new FileInputStream(templateFile), destination);
				}
			} catch (Exception e) {
				logger.error("Error while copying template file: " + filePath, e);
			}
		}
	}
	
	public void processSavedTemplates() {
		File templateDirectory = new File(TEMPLATE_LOCATION);
		
		if (templateDirectory == null || !templateDirectory.isDirectory()) {
			return;
		}
		
		for (File templateFile : templateDirectory.listFiles()) {
			if (templateFile.getName().endsWith(".xml")) {
				try {
					logger.debug("Processing template file: " + templateFile.getName());
					processJmxTemplate(new FileInputStream(templateFile));
					
					templateFile.delete();
				} catch (Exception e) {
					logger.error("Error while processing template file: " + templateFile.getName(), e);
				}
			}
		}
	}
	
	private void processJmxTemplate(InputStream jmxTemplateInputStream) {
		JmxTemplate jmxTemplate = JAXB.unmarshal(jmxTemplateInputStream, JmxTemplate.class);
		
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		JmxAccessor jmxAccessor = JmxAccessor.getInstance();
		
		for (GraphTemplate graphTemplate : jmxTemplate.getGraphs()) {
			Set<JmxAttribute> attributes = new HashSet<>();
			for (MBeanTemplate mBeanTemplate : graphTemplate.getMbeans()) {
				try {
					attributes.addAll(processMBean(mBeanTemplate, repository, jmxAccessor));
				} catch (Exception e) {
					logger.warn("Unable to process MBean with canonical name: " + mBeanTemplate.getCanonicalName() + " and attribute: " + mBeanTemplate.getAttributeName(), e);
				}
			}
			
			for (JmxAttribute attribute : attributes) {
				logger.warn("Attribute processed: " + attribute.toString());
			}
			
			JmxGraph jmxGraph = new JmxGraph(graphTemplate.getGraphName(), graphTemplate.getGraphType(), graphTemplate.getMultiplier(), graphTemplate.isIntegerValue(), attributes);
			
			processGraph(jmxGraph, repository);
		}
	}
	
	private Set<JmxAttribute> processMBean(MBeanTemplate mBeanTemplate, JmxAttributeRepository repository, JmxAccessor jmxAccessor) throws MalformedObjectNameException, 
			InstanceNotFoundException, IntrospectionException, AttributeNotFoundException, ReflectionException, MBeanException, IOException {
		
		JmxObjectName jmxObjectInRepository = repository.getJmxObjectNameWithAttributes(mBeanTemplate.getCanonicalName());
		JmxObjectName jmxObjectInMBeanServer = jmxAccessor.lookupAttribute(mBeanTemplate.getCanonicalName(), mBeanTemplate.getAttributeName(), mBeanTemplate.getAttributePaths());
		
		if (jmxObjectInRepository == null) {
			// Option is enabled and object/attribute do not exist - add them
			jmxObjectInRepository = repository.insertJmxObjectName(jmxObjectInMBeanServer); // Reassigning so that we have all the primary keys
		} else {
			// Make sure we have all the attributes that we should have
			for (JmxAttribute attribute : jmxObjectInMBeanServer.getAttributes()) {
				if (!jmxObjectInRepository.containsAttribute(attribute)) {
					logger.warn("Attribute " + attribute.getAttributeName() + " does not exist for mbean " + mBeanTemplate.getCanonicalName() + ". Adding to the repository.");
					jmxObjectInRepository.getAttributes().add(repository.insertJmxAttribute(jmxObjectInRepository.getObjectNameId(), attribute));
				}
			}
		}
		
		return jmxObjectInRepository.getAttributes();
	}
	
	private void processGraph(JmxGraph jmxGraph, JmxAttributeRepository repository) {
		logger.warn("Processing graph: " + jmxGraph.getGraphName());
		
		JmxGraph jmxGraphInRepository = repository.getJmxGraph(jmxGraph.getGraphName());
		
		if (jmxGraphInRepository == null) {
			logger.warn("Graph: " + jmxGraph.getGraphName() + " was not found. Adding to the repository.");
			jmxGraph = repository.insertJmxGraph(jmxGraph); // Reassigning because we need the primary key
		} else {
			for (JmxAttribute jmxAttribute : jmxGraph.getAttributes()) {
				if (!jmxGraphInRepository.containsAttribute(jmxAttribute)) {
					logger.warn("Attribute " + jmxAttribute.getAttributeName() + " does not exist for graph " + jmxGraph.getGraphName() + ". Adding to the repository.");
					repository.insertJmxGraphAttribute(jmxGraphInRepository.getGraphId(), jmxAttribute.getAttributeId());
				}
			}
		}
	}
}
