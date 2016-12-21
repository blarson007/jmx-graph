package com.jmxgraph.domain;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.attribute.JmxAttributeRepository;

public enum DefaultObject {

	CPU_POLLING("java.lang:type=OperatingSystem", "ProcessCpuLoad", null) {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isCpuPollingEnabled();
		}
	}, 
	MEMORY_POLLING("java.lang:type=Memory", "HeapMemoryUsage", new String[] { "committed", "used" }) {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isMemoryPollingEnabled();
		}
	}, 
	THREAD_POLLING("java.lang:type=Threading", "ThreadCount", null) {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isThreadPollingEnabled();
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
	
	public void handleObject(ApplicationConfig config, JmxAccessor jmxAccessor, JmxAttributeRepository repository) throws MalformedObjectNameException, 
			InstanceNotFoundException, IntrospectionException, AttributeNotFoundException, ReflectionException, MBeanException, IOException {
		
		JmxObjectName jmxObjectName = repository.getJmxObjectNameWithAttributes(objectName);
		
		if (isEnabled(config)) {
			if (jmxObjectName == null) {
				// Option is enabled and object/attribute do not exist - add them
				jmxObjectName = jmxAccessor.lookupAttribute(objectName, attribute, attributePaths);
				repository.insertJmxObjectName(jmxObjectName);
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
	}
}
