package com.jmxgraph.repository.attribute;

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import com.jmxgraph.config.Initializable;
import com.jmxgraph.domain.JmxAttribute;
import com.jmxgraph.domain.JmxAttributeValue;
import com.jmxgraph.domain.JmxObjectName;
import com.jmxgraph.ui.GraphFilter;


public interface JmxAttributeRepository extends Initializable<DataSource> {

	void insertJmxObjectName(JmxObjectName jmxObjectName);
	
	void insertJmxAttribute(final int objectNameId, final JmxAttribute jmxAttribute);
	
	void insertJmxAttributeValue(JmxAttributeValue jmxAttributeValue) throws Exception;
	
	void batchInsertAttributeValues(final List<JmxAttributeValue> jmxAttributeValues);
	
	JmxAttribute getJmxAttributeValuesByAttributeId(final int attributeId, GraphFilter filter);
	
	Collection<JmxObjectName> getAllEnabledAttributePaths();
	
	JmxObjectName getJmxObjectName(final String objectName);
	
	JmxObjectName getJmxObjectNameWithAttributes(final String objectName);
	
	JmxAttribute getJmxAttribute(final int objectNameId, String attributeName);
	
	void enableJmxAttributePath(int pathId);
	
	void disableJmxAttributePath(int pathId);
}
