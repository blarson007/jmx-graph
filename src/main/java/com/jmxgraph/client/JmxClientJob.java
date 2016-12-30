package com.jmxgraph.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.domain.jmx.JmxAttribute;
import com.jmxgraph.domain.jmx.JmxAttributeValue;
import com.jmxgraph.domain.jmx.JmxObjectName;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.jmx.JdbcAttributeRepository;
import com.jmxgraph.repository.jmx.JmxAttributeRepository;


public class JmxClientJob implements Job {
	
	private static final Logger logger = LoggerFactory.getLogger(JmxClientJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JmxAccessor jmxAccessor = JmxAccessor.getInstance();
		JmxAttributeRepository jmxAttributeRepository = JdbcAttributeRepository.getInstance();
		
		try {
			for (JmxObjectName jmxObjectName : jmxAttributeRepository.getAllEnabledAttributePaths()) {
				handleJmxObjectName(jmxObjectName, jmxAttributeRepository, jmxAccessor);	
			}
		} catch (Exception e) {
			logger.error("Failed to execute jmx collection", e);
		} 
	}
	
	private void handleJmxObjectName(JmxObjectName jmxObjectName, JmxAttributeRepository repository, JmxAccessor jmxAccessor) throws MalformedObjectNameException, 
			AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		Date now = new Date(); // Assign all attribute values the same date
		List<JmxAttributeValue> jmxAttributeValues = new ArrayList<>();
		
		Map<String, Object> attributeToValueMap = new HashMap<>();
		for (JmxAttribute jmxAttribute : jmxObjectName.getAttributes()) {
			if (StringUtils.isBlank(jmxAttribute.getPath())) {
				Object value = jmxAccessor.getAttributeValue(jmxObjectName.getCanonicalName(), jmxAttribute.getAttributeName());
				jmxAttributeValues.add(new JmxAttributeValue(jmxAttribute.getAttributeId(), value, now));
			} else {
				// Handle attributes that have multiple "paths".
				// On the JMX side this is likely done through the CompositeData type
				Object value = attributeToValueMap.get(jmxAttribute.getAttributeName());
				
				if (value == null) {
					value = jmxAccessor.getAttributeValue(jmxObjectName.getCanonicalName(), jmxAttribute.getAttributeName());
					attributeToValueMap.put(jmxAttribute.getAttributeName(), value);
				}
			
				if (value instanceof CompositeDataSupport) {
					CompositeDataSupport attributeValue = (CompositeDataSupport) value;
					jmxAttributeValues.add(new JmxAttributeValue(jmxAttribute.getAttributeId(), attributeValue.get(jmxAttribute.getPath()), now));
				}
			}
		}
		
		repository.batchInsertAttributeValues(jmxAttributeValues);
	}
}
