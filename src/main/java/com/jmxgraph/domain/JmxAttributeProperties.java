package com.jmxgraph.domain;

import java.util.HashMap;

@Deprecated // Replaced with JmxGraph
public class JmxAttributeProperties extends HashMap<String, String> {

	private static final long serialVersionUID = 8888126991636529071L;

	public static final String INTEGER_VALUE = "integer-value";
	public static final String MULTIPLIER = "multiplier";
	public static final String GRAPH_TYPE = "graph-type";
	
	public static final String GRAPH_TYPE_NONE = "none";
	public static final String GRAPH_TYPE_MEMORY = "memory";
	public static final String GRAPH_TYPE_PERCENTAGE = "percentage";
}
