package com.jmxgraph.repository;

import java.util.Collection;

import javax.sql.DataSource;

import com.jmxgraph.domain.JmxAttributePath;
import com.jmxgraph.domain.JmxAttributeValue;
import com.jmxgraph.ui.GraphFilter;
import com.jmxgraph.util.Initializable;


public interface JmxAttributeRepository extends Initializable<DataSource> {

	void insertJmxAttributePath(JmxAttributePath jmxAttributePath);
	
	void insertJmxAttributeValue(JmxAttributeValue jmxAttributeValue) throws Exception;
	
	Collection<JmxAttributePath> getAllJmxAttributeValues();
	
	JmxAttributePath getJmxAttributeValuesByPathId(final int pathId, GraphFilter filter);
	
	Collection<JmxAttributePath> getAllEnabledAttributePaths();
	
	JmxAttributePath getJmxAttributePath(String objectName, String attribute);
	
	void enableJmxAttributePath(int pathId);
	
	void disableJmxAttributePath(int pathId);
}
