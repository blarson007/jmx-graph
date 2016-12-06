package com.jmxgraph.client;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.config.SingletonManager;
import com.jmxgraph.domain.JmxAttributePath;
import com.jmxgraph.domain.JmxAttributeValue;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.JmxAttributeRepository;


public class JmxClientJob implements Job {
	
	private static final Logger logger = LoggerFactory.getLogger(JmxClientJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JmxAccessor jmxAccessor = SingletonManager.getJmxAccessor();
		JmxAttributeRepository jmxAttributeRepository = SingletonManager.getJmxAttributeRepository();
		try {
			for (JmxAttributePath jmxAttributePath : jmxAttributeRepository.getAllEnabledAttributePaths()) {
				Object value = jmxAccessor.getAttributeValue(jmxAttributePath.getObjectName(), jmxAttributePath.getAttribute());
				jmxAttributeRepository.insertJmxAttributeValue(new JmxAttributeValue(jmxAttributePath.getPathId(), value, new Date()));
			}
		} catch (Exception e) {
			logger.error("Failed to execute jmx collection", e);
		} 
	}
}
