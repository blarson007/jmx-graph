package com.jmxgraph.domain.jmx;

import java.util.HashSet;
import java.util.Set;

public class JmxObjectName {

	private int objectNameId;
	private String canonicalName;
	private String description;
	private Set<JmxAttribute> attributes;
	
	public JmxObjectName(final int objectNameId, final String canonicalName, final String description) {
		this(objectNameId, canonicalName, description, new HashSet<JmxAttribute>());
	}
	
	public JmxObjectName(final int objectNameId, final String canonicalName, final String description, Set<JmxAttribute> attributes) {
		this(canonicalName, description, attributes);
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
		// Wrap to 110 characters
		if (canonicalName.length() < 110) {
			return canonicalName;
		} else {
			return canonicalName.substring(0, 109) + "...";
		}
	}
	
	public JmxObjectName merge(JmxObjectName other) {
		if (!canonicalName.equals(other.getCanonicalName())) {
			throw new IllegalArgumentException("JMX Objects cannot be merged unless they have the same canonical name");
		}
		
		for (JmxAttribute attribute : other.getAttributes()) {
			if (!this.containsAttribute(attribute)) {
				addAttribute(attribute);
			}
		}
		return this;
	}
	
	public boolean containsAttribute(JmxAttribute attribute) {
		for (JmxAttribute jmxAttribute : attributes) {
			if (jmxAttribute.equals(attribute)) {
				return true;
			}
		}
		return false;
	}
}
