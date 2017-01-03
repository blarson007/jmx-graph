package com.jmxgraph.repository.jmx;

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import com.jmxgraph.config.Initializable;
import com.jmxgraph.domain.jmx.JmxAttribute;
import com.jmxgraph.domain.jmx.JmxAttributeValue;
import com.jmxgraph.domain.jmx.JmxGraph;
import com.jmxgraph.domain.jmx.JmxObjectName;
import com.jmxgraph.ui.GraphFilter;


public interface JmxAttributeRepository extends Initializable<DataSource> {

	JmxObjectName insertJmxObjectName(JmxObjectName jmxObjectName);
	
	JmxAttribute insertJmxAttribute(final int objectNameId, final JmxAttribute jmxAttribute);
	
	void insertJmxAttributeValue(JmxAttributeValue jmxAttributeValue) throws Exception;
	
	void batchInsertAttributeValues(final List<JmxAttributeValue> jmxAttributeValues);
	
	JmxAttribute getJmxAttributeValuesByAttributeId(final int attributeId, GraphFilter filter);
	
	Collection<JmxObjectName> getAllEnabledAttributePaths();
	
	JmxObjectName getJmxObjectName(final String objectName);
	
	JmxObjectName getJmxObjectNameWithAttributes(final String objectName);
	
	JmxAttribute getJmxAttribute(final int objectNameId, String attributeName);
	
	void enableJmxAttributePath(int pathId);
	
	void disableJmxAttributePath(int pathId);
	
	JmxGraph getJmxGraph(final String graphName);
	
	JmxGraph getJmxGraph(final int graphId);
	
	JmxGraph insertJmxGraph(JmxGraph jmxGraph);
	
	void insertJmxGraphAttribute(final int jmxGraphId, final int jmxAttributeId);
	
	void removeJmxGraphAttribute(final int jmxGraphId, final int jmxAttributeId);
	
	Collection<JmxGraph> getAllEnabledGraphs();
	
	void saveOrUpdate(JmxObjectName jmxObjectName);
}
