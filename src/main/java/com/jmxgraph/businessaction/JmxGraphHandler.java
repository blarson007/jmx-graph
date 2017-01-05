package com.jmxgraph.businessaction;

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
}
