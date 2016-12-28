package com.jmxgraph.businessaction;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.config.Initializable;
import com.jmxgraph.domain.ApplicationConfig;
import com.jmxgraph.domain.DefaultObject;
import com.jmxgraph.domain.JmxConnectionConfig;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.attribute.JdbcAttributeRepository;
import com.jmxgraph.repository.attribute.JmxAttributeRepository;
import com.jmxgraph.repository.config.ApplicationConfigRepository;
import com.jmxgraph.repository.config.XmlApplicationConfigRepository;

public class ApplicationConfigHandler implements Initializable<ApplicationConfig> {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfigHandler.class);
	
	private boolean jmxStarted = false;
	
	private ApplicationConfigHandler() {  }
	
	private static class InstanceHolder {
		private static final ApplicationConfigHandler instance = new ApplicationConfigHandler();
	}
	
	public static ApplicationConfigHandler getInstance() {
		return InstanceHolder.instance;
	}
	
	public ApplicationConfig getExistingApplicationConfig() {
		return XmlApplicationConfigRepository.getInstance().getApplicationConfig();
	}
	
	@Override
	public void initialize(ApplicationConfig newConfig) throws Exception {
		ApplicationConfigRepository repository = XmlApplicationConfigRepository.getInstance();
		ApplicationConfig existingConfig = repository.getApplicationConfig();
		
		if (newConfig.equals(existingConfig)) {
			logger.warn("No config changes detected.");
			return;
		}
		
		repository.saveApplicationConfig(newConfig);
		
		if (jmxStarted) {
			stopApplication();
		}

		startJmx(newConfig.getJmxConnectionConfig());
		
		populateDefaultObjects(newConfig);
		
		startPoller(newConfig.getPollIntervalInSeconds());
	}
	
	@Override
	public boolean isInitialized() {
		return jmxStarted;
	}
	
	private void startJmx(JmxConnectionConfig jmxConfig) throws Exception {
		logger.warn("Attempting to connect to JMX.");
		JmxAccessor.getInstance().initialize(jmxConfig);
		
		jmxStarted = true;
	}
	
	private void startPoller(Integer pollIntervalInSeconds) throws Exception {
		logger.warn("Attempting to start JMX polling.");
		PollScheduler.getInstance().initialize(pollIntervalInSeconds);
	}
	
	private void stopApplication() throws Exception {
		PollScheduler.getInstance().stopJobExection();
		JmxAccessor.getInstance().shutdown();
		
		jmxStarted = false;
	}
	
	private void populateDefaultObjects(ApplicationConfig config) throws Exception {
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		JmxAccessor jmxAccessor = JmxAccessor.getInstance();
		
		for (DefaultObject object : DefaultObject.values()) {
			object.handleObject(config, jmxAccessor, repository);
		}
	}
}
