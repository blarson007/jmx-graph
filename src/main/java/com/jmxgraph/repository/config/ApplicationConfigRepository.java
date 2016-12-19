package com.jmxgraph.repository.config;

import com.jmxgraph.domain.ApplicationConfig;

public interface ApplicationConfigRepository {

	ApplicationConfig getApplicationConfig();
	
	void saveApplicationConfig(ApplicationConfig applicationConfig) throws Exception;
}
