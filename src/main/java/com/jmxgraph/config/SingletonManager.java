package com.jmxgraph.config;

import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.JmxAttributeRepository;

public class SingletonManager {

	private static JmxAccessor jmxAccessor;
	private static JmxAttributeRepository jmxAttributeRepository;
	private static PollScheduler pollScheduler;
	
	public static JmxAccessor getJmxAccessor() {
		return jmxAccessor;
	}
	
	public static void setJmxAccessor(JmxAccessor jmxAccessor) {
		SingletonManager.jmxAccessor = jmxAccessor;
	}
	
	public static JmxAttributeRepository getJmxAttributeRepository() {
		return jmxAttributeRepository;
	}
	
	public static void setJmxAttributeRepository(JmxAttributeRepository jmxAttributeRepository) {
		SingletonManager.jmxAttributeRepository = jmxAttributeRepository;
	}

	public static PollScheduler getPollScheduler() {
		return pollScheduler;
	}

	public static void setPollScheduler(PollScheduler pollScheduler) {
		SingletonManager.pollScheduler = pollScheduler;
	}
}
