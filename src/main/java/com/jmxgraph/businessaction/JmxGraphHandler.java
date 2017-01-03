package com.jmxgraph.businessaction;

import java.util.Collection;
import java.util.HashSet;

import com.jmxgraph.domain.jmx.JmxAttribute;
import com.jmxgraph.domain.jmx.JmxGraph;
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
		Collection<Series> seriesCollection = new HashSet<>();
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		
		JmxGraph jmxGraph = repository.getJmxGraph(graphId);
		for (JmxAttribute jmxAttribute : jmxGraph.getAttributes()) {
			seriesCollection.add(jmxAttribute.buildGraphSeries());
		}
		
		return new JsonGraph(new GraphObject((Series[])seriesCollection.toArray()), filter.getLabelFormat(), jmxGraph.getGraphType(), jmxGraph.isIntegerValue());
	}
}
