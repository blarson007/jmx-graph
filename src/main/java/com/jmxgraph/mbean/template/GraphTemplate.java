package com.jmxgraph.mbean.template;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class GraphTemplate {

	@XmlElement(name = "graph_name", required = true)
	private String graphName;
	
	@XmlElementWrapper(name = "mbeans", required = true)
	@XmlElement(name = "mbean", required = true)
	private Collection<MBeanTemplate> mbeans;
	
	@XmlElement(name = "graph_type", defaultValue = "none")
	private String graphType;
	
	@XmlElement(name = "graph_multiplier", defaultValue = "1")
	private int multiplier = 1;
	
	@XmlElement(name = "integer_value", defaultValue = "false")
	private boolean integerValue;

	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	public Collection<MBeanTemplate> getMbeans() {
		return mbeans;
	}

	public void setMbeans(Collection<MBeanTemplate> mbeans) {
		this.mbeans = mbeans;
	}

	public String getGraphType() {
		return graphType;
	}

	public void setGraphType(String graphType) {
		this.graphType = graphType;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}

	public boolean isIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(boolean integerValue) {
		this.integerValue = integerValue;
	}
}
