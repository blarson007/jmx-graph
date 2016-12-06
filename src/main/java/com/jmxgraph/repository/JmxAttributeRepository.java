package com.jmxgraph.repository;

import java.util.Collection;

import com.jmxgraph.domain.JmxAttribute;
import com.jmxgraph.domain.JmxAttributeValue;
import com.jmxgraph.domain.JmxObjectName;
import com.jmxgraph.ui.GraphFilter;


public interface JmxAttributeRepository {

	void insertJmxObjectName(JmxObjectName jmxAttributePath);
	
	void insertJmxAttribute(final int objectNameId, final JmxAttribute jmxAttribute);
	
	void insertJmxAttributeValue(JmxAttributeValue jmxAttributeValue) throws Exception;
	
//	Collection<JmxObjectName> getAllJmxAttributeValues();
	
	JmxAttribute getJmxAttributeValuesByAttributeId(final int attributeId, GraphFilter filter);
	
	Collection<JmxObjectName> getAllEnabledAttributePaths();
	
	JmxObjectName getJmxObjectName(String objectName);
	
	JmxAttribute getJmxAttribute(final int objectNameId, String attributeName);
	
	void enableJmxAttributePath(int pathId);
	
	void disableJmxAttributePath(int pathId);
}
