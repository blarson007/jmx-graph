package com.jmxgraph.repository.appconfig;

import com.jmxgraph.domain.appconfig.ApplicationConfig;

public interface ApplicationConfigRepository {

	ApplicationConfig getApplicationConfig();
	
	void saveApplicationConfig(ApplicationConfig applicationConfig) throws Exception;
}
