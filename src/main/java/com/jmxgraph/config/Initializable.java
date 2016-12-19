package com.jmxgraph.config;

public interface Initializable<T> {

	boolean isInitialized();
	
	void initialize(T object) throws Exception;
}
