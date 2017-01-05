package com.jmxgraph.domain.jmx;

import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.jmxgraph.domain.JmxAttributeProperties;
import com.jmxgraph.ui.GraphFilter;
import com.jmxgraph.ui.GraphObject;
import com.jmxgraph.ui.GraphObject.DataPoint;
import com.jmxgraph.ui.GraphObject.Series;
import com.jmxgraph.ui.JsonGraph;

public class JmxAttribute {

	private int attributeId;
	private int objectNameId;
	private String attributeName;
	private String attributeType;
	private String path;
	private boolean enabled = true;
	private Collection<JmxAttributeValue> attributeValues = new TreeSet<>();
	private JmxAttributeProperties attributeProperties = new JmxAttributeProperties();
	
	private JmxObjectName jmxObjectName; // We will need a reference back to the parent in some cases
	
	public JmxAttribute(int attributeId, int objectNameId, String attributeName, String attributeType, String path, boolean enabled) {
		this(attributeName, attributeType, path);
		this.attributeId = attributeId;
		this.objectNameId = objectNameId;
		this.enabled = enabled;
	}
	
	public JmxAttribute(String attributeName, String attributeType, String path) {
		this(attributeName, attributeType);
		this.path = path;
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
	
	public JmxAttributeProperties getAttributeProperties() {
		return attributeProperties;
	}
	
	public JmxObjectName getJmxObjectName() {
		return jmxObjectName;
	}
	
	public void setJmxObjectName(JmxObjectName jmxObjectName) {
		// TODO: Set it manually for now. Try to work this into constructor, etc.
		this.jmxObjectName = jmxObjectName;
	}
	
	public String getGraphTypeProperty() {
		String value = attributeProperties.get(JmxAttributeProperties.GRAPH_TYPE);
		return value == null ? JmxAttributeProperties.GRAPH_TYPE_NONE : value;
	}
	
	public boolean getOnlyIntegerProperty() {
		String value = attributeProperties.get(JmxAttributeProperties.INTEGER_VALUE);
		return value == null ? false : Boolean.parseBoolean(value);
	}
	
	public boolean containsProperty(String propertyName) {
		return attributeProperties.containsKey(propertyName);
	}
	
	public String getAttributeDescription() {
		if (StringUtils.isBlank(path)) {
			return attributeName;
		}
		
		return attributeName + " - " + path; 
	}
	
	public boolean isNumericDataType() {
		// TODO: Use org.apache.commons.lang3.ClassUtils to help determine if the type represents a number
		// May need to convert to the wrapper class, then see if the wrapper class extends Number
		if (attributeType.equalsIgnoreCase("java.lang.String")) {
			return false;
		}
		
		return true;
	}
	
	@Deprecated // Use buildGraphSeries instead
	public JsonGraph getGraphObject(GraphFilter filter) {
		if (!isNumericDataType()) {
			return new JsonGraph("Cannot create a graph for a non-numeric data type");
		}
		
		DataPoint[] dataPoints = new DataPoint[attributeValues.size()];
		
		int index = 0;
		for (JmxAttributeValue value : attributeValues) {
			// This is a hack to get non integer values to display
			try {
				dataPoints[index] = new DataPoint(value.getTimestamp(), applyMultiplier(Integer.parseInt(String.valueOf(value.getAttributeValue()))));
			} catch (NumberFormatException e) {
				try {
					dataPoints[index] = new DataPoint(value.getTimestamp(), applyMultiplier(Long.parseLong(String.valueOf(value.getAttributeValue()))));
				} catch (NumberFormatException ne) {
					dataPoints[index] = new DataPoint(value.getTimestamp(), applyMultiplier(Double.parseDouble(String.valueOf(value.getAttributeValue()))));
				}
			}
			
			index++;
		}
		
		return new JsonGraph(new GraphObject(new Series(dataPoints)), filter.getLabelFormat(), getGraphTypeProperty(), getOnlyIntegerProperty());
	}
	
	public Series buildGraphSeries(int multiplier) {
		if (multiplier <= 0) { multiplier = 1; }
		// TODO: Handle non-numeric data types
		
		DataPoint[] dataPoints = new DataPoint[attributeValues.size()];
		
		int index = 0;
		for (JmxAttributeValue value : attributeValues) {
			// This is a hack to get non integer values to display
			try {
				dataPoints[index] = new DataPoint(value.getTimestamp(), multiplier * Integer.parseInt(String.valueOf(value.getAttributeValue())));
			} catch (NumberFormatException e) {
				try {
					dataPoints[index] = new DataPoint(value.getTimestamp(), multiplier * Long.parseLong(String.valueOf(value.getAttributeValue())));
				} catch (NumberFormatException ne) {
					dataPoints[index] = new DataPoint(value.getTimestamp(), multiplier * Double.parseDouble(String.valueOf(value.getAttributeValue())));
				}
			}
			
			index++;
		}
		
		return new Series(dataPoints);
	}
	
	private <N extends Number> Number applyMultiplier(N number) {
		String multiplier = attributeProperties.get(JmxAttributeProperties.MULTIPLIER);
		return multiplier == null ? number : Integer.parseInt(multiplier) * number.doubleValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeName == null) ? 0 : attributeName.hashCode());
		result = prime * result + ((attributeType == null) ? 0 : attributeType.hashCode());
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
		JmxAttribute other = (JmxAttribute) obj;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		if (attributeType == null) {
			if (other.attributeType != null)
				return false;
		} else if (!attributeType.equals(other.attributeType))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JmxAttribute [attributeId=" + attributeId + ", objectNameId=" + objectNameId + ", attributeName="
				+ attributeName + ", attributeType=" + attributeType + ", path=" + path + ", enabled=" + enabled
				+ ", attributeValues=" + attributeValues + ", attributeProperties=" + attributeProperties
				+ ", jmxObjectName=" + jmxObjectName + "]";
	}
}
