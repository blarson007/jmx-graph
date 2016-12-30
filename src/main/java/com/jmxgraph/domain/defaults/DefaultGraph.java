package com.jmxgraph.domain.defaults;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import com.jmxgraph.domain.appconfig.ApplicationConfig;
import com.jmxgraph.domain.jmx.JmxGraph;
import com.jmxgraph.domain.jmx.JmxObjectName;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.jmx.JmxAttributeRepository;

public enum DefaultGraph {

	CPU_GRAPH(JmxGraph.GRAPH_TYPE_PERCENTAGE, 1000, false, DefaultObject.PROCESS_CPU_LOAD, DefaultObject.SYSTEM_CPU_LOAD), 
	MEMORY_GRAPH(JmxGraph.GRAPH_TYPE_MEMORY, 1, false, DefaultObject.HEAP_MEMORY_USAGE), 
	THREAD_GRAPH(JmxGraph.GRAPH_TYPE_NONE, 1, true, DefaultObject.THREAD_COUNT);
	
	private String graphType;
	private int multiplier;
	private boolean integerValue;
	private DefaultObject[] defaultObjects;
	
	DefaultGraph(String graphType, int multiplier, boolean integerValue, DefaultObject...defaultObjects) {
		this.graphType = graphType;
		this.multiplier = multiplier;
		this.integerValue = integerValue;
		this.defaultObjects = defaultObjects;
	}
	
	public void handleObject(ApplicationConfig config, JmxAccessor jmxAccessor, JmxAttributeRepository repository) throws MalformedObjectNameException, 
			InstanceNotFoundException, IntrospectionException, AttributeNotFoundException, ReflectionException, MBeanException, IOException {
		
		JmxGraph jmxGraph = null;
		for (DefaultObject defaultObject : defaultObjects) {
			JmxObjectName jmxObjectName = defaultObject.handleObject(config, jmxAccessor, repository);
			jmxGraph = repository.getJmxGraph(this.name());
		
			if (jmxObjectName == null) {
				// Remove all attributes if they exist
			} else {
				// Create graph if it does not exist
				if (jmxGraph == null) {
					jmxGraph = new JmxGraph(graphType, multiplier, integerValue);
				}
				
				// Add attributes if they do not exist
			}
		}
	}
}
