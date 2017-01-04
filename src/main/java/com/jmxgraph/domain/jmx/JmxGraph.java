package com.jmxgraph.domain.jmx;

import java.util.Collection;
import java.util.HashSet;

public class JmxGraph {
	
	public static final String GRAPH_TYPE_NONE = "none";
	public static final String GRAPH_TYPE_MEMORY = "memory";
	public static final String GRAPH_TYPE_PERCENTAGE = "percentage";

	private int graphId;
	private String graphName;
	private String graphType;
	private int multiplier;
	private boolean integerValue;
	private Collection<JmxAttribute> attributes;
	
	public JmxGraph(int graphId, String graphName, String graphType, int multiplier, boolean integerValue, Collection<JmxAttribute> attributes) {
		this(graphName, graphType, multiplier, integerValue, attributes);
		this.graphId = graphId;
	}
	
	public JmxGraph(int graphId, String graphName, String graphType, int multiplier, boolean integerValue) {
		this(graphName, graphType, multiplier, integerValue, new HashSet<JmxAttribute>());
		this.graphId = graphId;
	}
	
	public JmxGraph(String graphName, String graphType, int multiplier, boolean integerValue, Collection<JmxAttribute> attributes) {
		this.graphName = graphName;
		this.graphType = graphType;
		this.multiplier = multiplier;
		this.integerValue = integerValue;
		this.attributes = attributes;
	}
	
	public int getGraphId() {
		return graphId;
	}
	
	public String getGraphName() {
		return graphName;
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
	
	public boolean containsAttribute(JmxAttribute attribute) {
		for (JmxAttribute jmxAttribute : attributes) {
			if (jmxAttribute.equals(attribute)) {
				return true;
			}
		}
		return false;
	}
}
