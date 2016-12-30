package com.jmxgraph.domain.jmx;

import java.util.Collection;

public class JmxGraph {

	private int graphId;
	private String graphType;
	private int multiplier;
	private boolean integerValue;
	private Collection<JmxAttribute> attributes;
	
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
}
