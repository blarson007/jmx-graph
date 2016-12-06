package com.jmxgraph.domain;

import java.util.Date;


public class JmxAttributeValue implements Comparable<JmxAttributeValue> {

	private int valueId;
	private int pathId;
	private Object attributeValue;
	private Date timestamp;
	
	public JmxAttributeValue(int pathId, Object attributeValue, Date timestamp) {
		this(-1, pathId, attributeValue, timestamp);
	}
	
	public JmxAttributeValue(int valueId, int pathId, Object attributeValue, Date timestamp) {
		this.valueId = valueId;
		this.pathId = pathId;
		this.attributeValue = attributeValue;
		this.timestamp = timestamp;
	}
	
	public int getValueId() {
		return valueId;
	}
	
	public int getPathId() {
		return pathId;
	}
	
	public Object getAttributeValue() {
		return attributeValue;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public int compareTo(JmxAttributeValue other) {
		return timestamp.compareTo(other.getTimestamp());
	}
}
