package com.jmxgraph.domain.appconfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.jmxgraph.repository.jmx.JmxAttributeRepositoryType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationConfig {
	
	public static final String REPOSITORY_TYPE_KEY = "persistence-type";
	public static final String POLL_INTERVAL_KEY = "jmx-poll-interval-sec";
	
	public static final int POLL_INTERVAL_DEFAULT = 5;
	
	private JmxAttributeRepositoryType repositoryType;
	private Integer pollIntervalInSeconds = POLL_INTERVAL_DEFAULT;
	private JmxConnectionConfig jmxConnectionConfig = new JmxConnectionConfig();
	private boolean cpuPollingEnabled = true;
	private boolean memoryPollingEnabled = true;
	private boolean threadPollingEnabled = true;
	
	public JmxAttributeRepositoryType getRepositoryType() {
		return repositoryType;
	}
	
	public void setRepositoryType(JmxAttributeRepositoryType repositoryType) {
		this.repositoryType = repositoryType;
	}
	
	public JmxConnectionConfig getJmxConnectionConfig() {
		return jmxConnectionConfig;
	}

	public void setJmxConnectionConfig(JmxConnectionConfig jmxConnectionConfig) {
		this.jmxConnectionConfig = jmxConnectionConfig;
	}

	public Integer getPollIntervalInSeconds() {
		return pollIntervalInSeconds;
	}
	
	public void setPollIntervalInSeconds(Integer pollIntervalInSeconds) {
		this.pollIntervalInSeconds = pollIntervalInSeconds;
	}
	
	public boolean isCpuPollingEnabled() {
		return cpuPollingEnabled;
	}

	public void setCpuPollingEnabled(boolean cpuPollingEnabled) {
		this.cpuPollingEnabled = cpuPollingEnabled;
	}

	public boolean isMemoryPollingEnabled() {
		return memoryPollingEnabled;
	}

	public void setMemoryPollingEnabled(boolean memoryPollingEnabled) {
		this.memoryPollingEnabled = memoryPollingEnabled;
	}

	public boolean isThreadPollingEnabled() {
		return threadPollingEnabled;
	}

	public void setThreadPollingEnabled(boolean threadPollingEnabled) {
		this.threadPollingEnabled = threadPollingEnabled;
	}

	public boolean deviatesFromDefault() {
		return 
				repositoryType != JmxAttributeRepositoryType.EMBEDDED_DB ||
				pollIntervalInSeconds != POLL_INTERVAL_DEFAULT ||
				(jmxConnectionConfig.getJmxHost() != null && !jmxConnectionConfig.getJmxHost().equals("")) ||
				(jmxConnectionConfig.getJmxPort() != null && jmxConnectionConfig.getJmxPort() > 0);
	}
}
