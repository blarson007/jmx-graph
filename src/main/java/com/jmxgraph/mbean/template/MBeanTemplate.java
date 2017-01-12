package com.jmxgraph.mbean.template;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class MBeanTemplate {

	@XmlElement(name = "canonical_name", required = true)
	private String canonicalName;
	
	@XmlElement(name = "attribute_name", required = true)
	private String attributeName;
	
	@XmlElementWrapper(name = "attribute_paths")
	@XmlElement(name = "attribute_path")
	private Collection<String> attributePaths;

	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Collection<String> getAttributePaths() {
		return attributePaths;
	}

	public void setAttributePaths(Collection<String> attributePaths) {
		this.attributePaths = attributePaths;
	}
}
