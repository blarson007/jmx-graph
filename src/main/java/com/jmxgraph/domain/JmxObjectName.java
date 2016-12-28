package com.jmxgraph.domain;

import java.util.HashSet;
import java.util.Set;

public class JmxObjectName {

	private int objectNameId;
	private String canonicalName;
	private String description;
	private Set<JmxAttribute> attributes;
	
	public JmxObjectName(final int objectNameId, final String canonicalName, final String description) {
		this(canonicalName, description, new HashSet<JmxAttribute>());
		this.objectNameId = objectNameId;
	}
	
	public JmxObjectName(final String canonicalName, final String description, Set<JmxAttribute> attributes) {
		this.canonicalName = canonicalName;
		this.description = description;
		this.attributes = attributes;
	}
	
	public int getObjectNameId() {
		return objectNameId;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<JmxAttribute> getAttributes() {
		return attributes;
	}
	
	public void addAttribute(JmxAttribute attribute) {
		attributes.add(attribute);
	}
	
	public String getObjectNameEscaped() {
		return canonicalName.replaceAll("\"", "&quot;");
	}
	
	public String getObjectNameShortened() {
		// Wrap to 120 characters
		if (canonicalName.length() < 120) {
			return canonicalName;
		} else {
			return canonicalName.substring(0, 119) + "...";
		}
	}
	
	public void applyAttributeProperties(JmxAttributeProperties attributeProperties) {
		for (JmxAttribute attribute : attributes) {
			attribute.getAttributeProperties().putAll(attributeProperties);
		}
	}
}
