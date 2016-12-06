package com.jmxgraph.domain;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.TreeSet;

import com.jmxgraph.ui.GraphObject;
import com.jmxgraph.ui.GraphObject.Series;

public class JmxAttribute {

	private int attributeId;
	private int objectNameId;
	private String attributeName;
	private String attributeType;
	private String path;
	private boolean enabled = true;
	private Collection<JmxAttributeValue> attributeValues = new TreeSet<>();
	
//	public JmxAttribute(String objectName, String attribute, String attributeType) {
//		this(objectName, attribute, attributeType, null);
//	}
	
//	public JmxAttribute(String objectName, String attribute, String attributeType, String path) {
//		this(-1, objectName, attribute, attributeType, path, true);
//	}
	
	public JmxAttribute(int attributeId, int objectNameId, String attributeName, String attributeType, String path, boolean enabled) {
		this(attributeName, attributeType);
		this.attributeId = attributeId;
		this.objectNameId = objectNameId;
		this.path = path;
		this.enabled = enabled;
	}
	
	public JmxAttribute(String attributeName, String attributeType) {
		this.attributeName = attributeName;
		this.attributeType = attributeType;
	}
	
	public int getAttributeId() {
		return attributeId;
	}
	
	public int getObjectNameId() {
		return objectNameId;
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	
	public String getAttributeType() {
		return attributeType;
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public Collection<JmxAttributeValue> getAttributeValues() {
		return attributeValues;
	}
	
	public boolean isNumericDataType() {
		// TODO: Use org.apache.commons.lang3.ClassUtils to help determine if the type represents a number
		// May need to convert to the wrapper class, then see if the wrapper class extends Number
		if (attributeType.equalsIgnoreCase("java.lang.String")) {
			return false;
		}
		
		return true;
	}
	
	public GraphObject getGraphObject() {
		if (!isNumericDataType()) {
			return new GraphObject("Cannot create a graph for a non-numeric data type");
		}
		
		String[] labels = new String[attributeValues.size()];
		Object[] data = new Object[attributeValues.size()];
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		
		int index = 0;
		for (JmxAttributeValue value : attributeValues) {
			labels[index] = sdf.format(value.getTimestamp());
			// This is a hack to get non integer values to display
			try {
				data[index] = Integer.parseInt(String.valueOf(value.getAttributeValue()));
			} catch (NumberFormatException e) {
				try {
					data[index] = Long.parseLong(String.valueOf(value.getAttributeValue()));
				} catch (NumberFormatException ne) {
					data[index] = Double.parseDouble(String.valueOf(value.getAttributeValue()));
				}
			}
			
			index++;
		}
		
		return new GraphObject(labels, new Series(data));
	}

}
