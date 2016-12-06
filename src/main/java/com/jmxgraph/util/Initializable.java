package com.jmxgraph.util;

public interface Initializable<T> {

	boolean isInitialized();
	
	void initialize(T arg) throws Exception;
}
