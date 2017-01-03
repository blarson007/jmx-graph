package com.jmxgraph.businessaction;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import com.jmxgraph.domain.jmx.JmxObjectName;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.mbean.template.GraphTemplate;
import com.jmxgraph.mbean.template.JmxTemplate;
import com.jmxgraph.mbean.template.MBeanTemplate;
import com.jmxgraph.repository.jmx.JdbcAttributeRepository;
import com.jmxgraph.repository.jmx.JmxAttributeRepository;

public class JmxTemplateHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(JmxTemplateHandler.class);

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
			processJmxTemplate(defaultObject.getTemplateFile());
		}
	}
	
	public void processJmxTemplate(File jmxTemplateFile) {
		JmxTemplate jmxTemplate = JAXB.unmarshal(jmxTemplateFile, JmxTemplate.class);
		
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		JmxAccessor jmxAccessor = JmxAccessor.getInstance();
		
		Map<String, JmxObjectName> objectNameMap = new HashMap<>();
		
		for (GraphTemplate graphTemplate : jmxTemplate.getGraphs()) {
			for (MBeanTemplate mBeanTemplate : graphTemplate.getMbeans()) {
				String[] paths = null;
				if (mBeanTemplate.getAttributePaths() != null) {
					paths = (String[]) mBeanTemplate.getAttributePaths().toArray();
				}
				
				try {
					JmxObjectName jmxObjectName = jmxAccessor.lookupAttribute(mBeanTemplate.getCanonicalName(), mBeanTemplate.getAttributeName(), paths);
					JmxObjectName existingObjectName = objectNameMap.get(mBeanTemplate.getCanonicalName());
					
					if (existingObjectName == null) {
						objectNameMap.put(mBeanTemplate.getCanonicalName(), jmxObjectName);
					} else {
						existingObjectName.merge(jmxObjectName);
					}
					
				} catch (Exception e) {
					logger.warn("Unable to process MBean with canonical name: " + mBeanTemplate.getCanonicalName() + " and attribute: " + mBeanTemplate.getAttributeName(), e);
				}
			}
		}
	}
}
