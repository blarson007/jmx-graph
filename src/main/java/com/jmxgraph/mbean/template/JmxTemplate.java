package com.jmxgraph.mbean.template;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.jmxgraph.domain.appconfig.ApplicationConfig;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JmxTemplate {

	private ApplicationConfig applicationConfig;
	
	@XmlElementWrapper(name = "graphs", required = true)
	@XmlElement(name = "graph", required = true)
	private Collection<GraphTemplate> graphs;

	public ApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public Collection<GraphTemplate> getGraphs() {
		return graphs;
	}

	public void setGraphs(Collection<GraphTemplate> graphs) {
		this.graphs = graphs;
	}
}
