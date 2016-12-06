package com.jmxgraph.ui;

public class ObjectNameHolder {

	private String value;
	
	public ObjectNameHolder(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public  String getObjectNameShortened() {
		// Wrap to 120 characters
		if (value.length() < 120) {
			return value;
		} else {
			return value.substring(0, 119) + "...";
		}
	}
	
	public String getObjectNameEscaped() {
		return value.replaceAll("\"", "&quot;");
	}
}
