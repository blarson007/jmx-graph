package com.jmxgraph.businessaction;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.config.Initializable;
import com.jmxgraph.domain.appconfig.ApplicationConfig;
import com.jmxgraph.domain.appconfig.JmxConnectionConfig;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.appconfig.ApplicationConfigRepository;
import com.jmxgraph.repository.appconfig.XmlApplicationConfigRepository;

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
		
		JmxTemplateHandler.getInstance().processDefaultJmxTemplates();
		JmxTemplateHandler.getInstance().processSavedTemplates();
		
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
}
