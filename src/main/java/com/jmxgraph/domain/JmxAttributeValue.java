package com.jmxgraph.domain;

import java.util.Date;


public class JmxAttributeValue implements Comparable<JmxAttributeValue> {

	private int valueId;
	private int attributeId;
	private Object attributeValue;
	private Date timestamp;
	
	public JmxAttributeValue(int attributeId, Object attributeValue, Date timestamp) {
		this(-1, attributeId, attributeValue, timestamp);
	}
	
	public JmxAttributeValue(int valueId, int attributeId, Object attributeValue, Date timestamp) {
		this.valueId = valueId;
		this.attributeId = attributeId;
		this.attributeValue = attributeValue;
		this.timestamp = timestamp;
	}
	
	public int getValueId() {
		return valueId;
	}
	
	public int getAttributeId() {
		return attributeId;
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
