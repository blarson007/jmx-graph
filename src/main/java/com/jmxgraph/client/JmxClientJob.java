package com.jmxgraph.client;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.domain.JmxAttribute;
import com.jmxgraph.domain.JmxAttributeValue;
import com.jmxgraph.domain.JmxObjectName;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.attribute.JdbcAttributeRepository;
import com.jmxgraph.repository.attribute.JmxAttributeRepository;


public class JmxClientJob implements Job {
	
	private static final Logger logger = LoggerFactory.getLogger(JmxClientJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
//		JmxAccessor jmxAccessor = SingletonManager.getJmxAccessor();
//		JmxAttributeRepository jmxAttributeRepository = SingletonManager.getJmxAttributeRepository();
		
		JmxAccessor jmxAccessor = JmxAccessor.getInstance();
		JmxAttributeRepository jmxAttributeRepository = JdbcAttributeRepository.getInstance();
		
		try {
			for (JmxObjectName jmxObjectName : jmxAttributeRepository.getAllEnabledAttributePaths()) {
				for (JmxAttribute attribute : jmxObjectName.getAttributes()) {
					Object value = jmxAccessor.getAttributeValue(jmxObjectName.getCanonicalName(), attribute.getAttributeName());
					jmxAttributeRepository.insertJmxAttributeValue(new JmxAttributeValue(attribute.getAttributeId(), value, new Date()));
				}	
			}
		} catch (Exception e) {
			logger.error("Failed to execute jmx collection", e);
		} 
	}
}
