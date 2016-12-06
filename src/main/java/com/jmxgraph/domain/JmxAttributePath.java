package com.jmxgraph.domain;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.TreeSet;

import com.jmxgraph.ui.GraphObject;
import com.jmxgraph.ui.GraphObject.Series;

public class JmxAttributePath {

	private int pathId;
	private String objectName;
	private String attribute;
	private String attributeType;
	private String path;
	private boolean enabled = true;
	private Collection<JmxAttributeValue> attributeValues = new TreeSet<>();
	
	public JmxAttributePath(String objectName, String attribute, String attributeType) {
		this(objectName, attribute, attributeType, null);
	}
	
	public JmxAttributePath(String objectName, String attribute, String attributeType, String path) {
		this(-1, objectName, attribute, attributeType, path, true);
	}
	
	public JmxAttributePath(int pathId, String objectName, String attribute, String attributeType, String path, boolean enabled) {
		this.pathId = pathId;
		this.objectName = objectName;
		this.attribute = attribute;
		this.attributeType = attributeType;
		this.path = path;
		this.enabled = enabled;
	}
	
	public int getPathId() {
		return pathId;
	}
	
	public String getObjectName() {
		return objectName;
	}
	
	public String getAttribute() {
		return attribute;
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
	
	public String getObjectNameEscaped() {
		return objectName.replaceAll("\"", "&quot;");
	}
	
	public String getObjectNameShortened() {
		// Wrap to 120 characters
		if (objectName.length() < 120) {
			return objectName;
		} else {
			return objectName.substring(0, 119) + "...";
		}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((attributeType == null) ? 0 : attributeType.hashCode());
		result = prime * result + ((objectName == null) ? 0 : objectName.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JmxAttributePath other = (JmxAttributePath) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (attributeType == null) {
			if (other.attributeType != null)
				return false;
		} else if (!attributeType.equals(other.attributeType))
			return false;
		if (objectName == null) {
			if (other.objectName != null)
				return false;
		} else if (!objectName.equals(other.objectName))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
}
