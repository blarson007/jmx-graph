package com.jmxgraph.domain.jmx;

import java.util.Collection;
import java.util.HashSet;

public class JmxGraph {
	
	public static final String GRAPH_TYPE_NONE = "none";
	public static final String GRAPH_TYPE_MEMORY = "memory";
	public static final String GRAPH_TYPE_PERCENTAGE = "percentage";

	private int graphId;
	private String graphType;
	private int multiplier;
	private boolean integerValue;
	private Collection<JmxAttribute> attributes;
	
	public JmxGraph(int graphId, String graphType, int multiplier, boolean integerValue) {
		this(graphType, multiplier, integerValue);
		this.graphId = graphId;
	}
	
	public JmxGraph(String graphType, int multiplier, boolean integerValue) {
		this.graphType = graphType;
		this.multiplier = multiplier;
		this.integerValue = integerValue;
		this.attributes = new HashSet<>();
	}
	
	public int getGraphId() {
		return graphId;
	}
	
	public String getGraphType() {
		return graphType;
	}
	
	public int getMultiplier() {
		return multiplier;
	}
	
	public boolean isIntegerValue() {
		return integerValue;
	}
	
	public Collection<JmxAttribute> getAttributes() {
		return attributes;
	}
	
	public void addAttribute(JmxAttribute jmxAttribute) {
		attributes.add(jmxAttribute);
	}
}