package com.jmxgraph.businessaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.jmxgraph.domain.jmx.JmxAttribute;
import com.jmxgraph.domain.jmx.JmxGraph;
import com.jmxgraph.domain.jmx.JmxObjectName;
import com.jmxgraph.repository.jmx.JdbcAttributeRepository;
import com.jmxgraph.repository.jmx.JmxAttributeRepository;
import com.jmxgraph.ui.GraphFilter;
import com.jmxgraph.ui.GraphObject;
import com.jmxgraph.ui.GraphObject.Series;
import com.jmxgraph.ui.JsonGraph;

public class JmxGraphHandler {

	private JmxGraphHandler() {  }
	
	private static class InstanceHolder {
		private static final JmxGraphHandler instance = new JmxGraphHandler();
	}
	
	public static JmxGraphHandler getInstance() {
		return InstanceHolder.instance;
	}
	
	public JsonGraph buildGraph(int graphId, GraphFilter filter) {
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		
		JmxGraph jmxGraph = repository.getJmxGraph(graphId);
		Series[] seriesArray = new Series[jmxGraph.getAttributes().size()];
		
		int index = 0;
		for (JmxAttribute jmxAttribute : jmxGraph.getAttributes()) {
			JmxAttribute jmxAttributeWithValue = repository.getJmxAttributeValuesByAttributeId(jmxAttribute.getAttributeId(), filter);
			seriesArray[index] = jmxAttributeWithValue.buildGraphSeries(jmxGraph.getMultiplier());
			index++;
		}
		
		return new JsonGraph(new GraphObject(seriesArray), filter.getLabelFormat(), jmxGraph.getGraphType(), jmxGraph.isIntegerValue());
	}
	
	public void toggleJmxGraph(String objectName, String attributeName, String attributeType) {
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		
		JmxAttribute jmxAttribute = new JmxAttribute(attributeName, attributeType);
		JmxObjectName jmxObjectName = repository.getJmxObjectNameWithAttributes(objectName);
		
		if (jmxObjectName == null) {
			Set<JmxAttribute> attributes = new HashSet<>();
			attributes.add(jmxAttribute);
			
			jmxObjectName = repository.insertJmxObjectName(new JmxObjectName(objectName, "", attributes));
		} else if (!jmxObjectName.containsAttribute(jmxAttribute)) {
			jmxAttribute = repository.insertJmxAttribute(jmxObjectName.getObjectNameId(), jmxAttribute);
		}
		
		Collection<JmxGraph> jmxGraphs = repository.getAllGraphs();
		JmxGraph targetGraph = null;
		JmxAttribute targetAttribute = null;
		
		for (JmxGraph jmxGraph : jmxGraphs) {
			if (jmxGraph.getGraphName().equals(buildGraphName(objectName, attributeName))) {
				targetGraph = jmxGraph;
				
				if (jmxGraph.containsAttribute(jmxAttribute)) {
					targetAttribute = jmxGraph.getAttributes().iterator().next();
				}
				break;
			}
		}
		
		if (targetGraph != null && targetAttribute != null) {
			// Remove attribute from the graph
			repository.removeJmxGraphAttribute(targetGraph.getGraphId(), targetAttribute.getAttributeId());
		} else if (targetGraph != null) {
			// Add the existing attribute to the existing graph
			targetAttribute = repository.getJmxAttribute(jmxObjectName.getObjectNameId(), attributeName);
			repository.insertJmxGraphAttribute(targetGraph.getGraphId(), targetAttribute.getAttributeId());
		} else {
			// Create a new graph with the attribute
			Set<JmxAttribute> attributes = new HashSet<>();
			attributes.add(jmxAttribute);
			
			repository.insertJmxGraph(new JmxGraph(buildGraphName(objectName, attributeName), "none", 1, false, attributes));
		}
	}
	
	private String buildGraphName(String objectName, String attributeName) {
		return objectName + " - " + attributeName;
	}
}
