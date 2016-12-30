package com.jmxgraph.domain.defaults;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import com.jmxgraph.domain.JmxAttributeProperties;
import com.jmxgraph.domain.appconfig.ApplicationConfig;
import com.jmxgraph.domain.jmx.JmxAttribute;
import com.jmxgraph.domain.jmx.JmxObjectName;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.jmx.JmxAttributeRepository;

public enum DefaultObject {

	PROCESS_CPU_LOAD("java.lang:type=OperatingSystem", "ProcessCpuLoad", null) {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isCpuPollingEnabled();
		}

		public JmxAttributeProperties getDefaultProperties() {
			JmxAttributeProperties attributeProperties = new JmxAttributeProperties();
			
			attributeProperties.put(JmxAttributeProperties.MULTIPLIER, "1000");
			attributeProperties.put(JmxAttributeProperties.GRAPH_TYPE, JmxAttributeProperties.GRAPH_TYPE_PERCENTAGE);
			
			return attributeProperties;
		}
	},
	SYSTEM_CPU_LOAD("java.lang:type=OperatingSystem", "SystemCpuLoad", null) {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isCpuPollingEnabled();
		}
	},
	HEAP_MEMORY_USAGE("java.lang:type=Memory", "HeapMemoryUsage", new String[] { "committed", "used" }) {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isMemoryPollingEnabled();
		}

		public JmxAttributeProperties getDefaultProperties() {
			JmxAttributeProperties attributeProperties = new JmxAttributeProperties();
			
			attributeProperties.put(JmxAttributeProperties.GRAPH_TYPE, JmxAttributeProperties.GRAPH_TYPE_MEMORY);
			
			return attributeProperties;
		}
	}, 
	THREAD_COUNT("java.lang:type=Threading", "ThreadCount", null) {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isThreadPollingEnabled();
		}

		public JmxAttributeProperties getDefaultProperties() {
			JmxAttributeProperties attributeProperties = new JmxAttributeProperties();
			
			attributeProperties.put(JmxAttributeProperties.INTEGER_VALUE, "true");
			
			return attributeProperties;
		}
	};
	
	private String objectName;
	private String attribute;
	private String[] attributePaths;
	
	DefaultObject(String objectName, String attribute, String[] attributePaths) {
		this.objectName = objectName;
		this.attribute = attribute;
		this.attributePaths = attributePaths;
	}
	
	public abstract boolean isEnabled(ApplicationConfig config);
//	public abstract JmxAttributeProperties getDefaultProperties();
	
	public JmxObjectName handleObject(ApplicationConfig config, JmxAccessor jmxAccessor, JmxAttributeRepository repository) throws MalformedObjectNameException, 
			InstanceNotFoundException, IntrospectionException, AttributeNotFoundException, ReflectionException, MBeanException, IOException {
		
		JmxObjectName jmxObjectName = repository.getJmxObjectNameWithAttributes(objectName);
		
		if (isEnabled(config)) {
			if (jmxObjectName == null) {
				// Option is enabled and object/attribute do not exist - add them
				jmxObjectName = jmxAccessor.lookupAttribute(objectName, attribute, attributePaths);
//				jmxObjectName.applyAttributeProperties(getDefaultProperties());
				jmxObjectName = repository.insertJmxObjectName(jmxObjectName); // Reassigning so that we have all the primary keys
			} else {
				for (JmxAttribute jmxAttribute : jmxObjectName.getAttributes()) {
					if (!jmxAttribute.isEnabled()) {
						// Option is enabled and object/attribute exist and not enabled - enable them
						repository.enableJmxAttributePath(jmxAttribute.getAttributeId());
					} else {
						// Option is enabled and object/attribute exist and are enabled - do nothing
					}
				}
			}
		} else {
			if (jmxObjectName == null) {
				// Option is not enabled and object/attribute do not exist - do nothing
			} else {
				for (JmxAttribute jmxAttribute : jmxObjectName.getAttributes()) {
					if (jmxAttribute.isEnabled()) {
						// Option is not enabled and object/attribute exist and are enabled - disable them
						repository.disableJmxAttributePath(jmxAttribute.getAttributeId());
					} else {
						// Option is not enabled and object/attribute exist and are not enabled - do nothing
					}
				}
			}
		}
		return jmxObjectName;
	}
}
