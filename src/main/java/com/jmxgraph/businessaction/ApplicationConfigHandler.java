package com.jmxgraph.businessaction;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.config.Initializable;
import com.jmxgraph.domain.ApplicationConfig;
import com.jmxgraph.mbean.JmxAccessor;
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
			// TODO: Reload the application
		} else {
			startApplication(newConfig);
		}
	}
	
	@Override
	public boolean isInitialized() {
		return jmxStarted;
	}
	
	private void startApplication(ApplicationConfig config) throws Exception {
		logger.warn("Attempting to connect to JMX.");
		JmxAccessor.getInstance().initialize(config.getJmxConnectionConfig());
		
		logger.warn("Attempting to start JMX polling.");
		PollScheduler.getInstance().initialize(config.getPollIntervalInSeconds());
		
		jmxStarted = true;
	}
}
