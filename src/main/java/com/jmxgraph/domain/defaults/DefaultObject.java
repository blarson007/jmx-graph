package com.jmxgraph.domain.defaults;

import java.io.File;
import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.springframework.core.io.ClassPathResource;

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
		
		public File getTemplateFile() throws IOException {
			return new ClassPathResource("template/cpu-default-object.xml").getFile();
		}
	},
	SYSTEM_CPU_LOAD("java.lang:type=OperatingSystem", "SystemCpuLoad", null) {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isCpuPollingEnabled();
		}
		
		public File getTemplateFile() throws IOException {
			return new ClassPathResource("template/cpu-default-object.xml").getFile();
		}
	},
	HEAP_MEMORY_USAGE("java.lang:type=Memory", "HeapMemoryUsage", new String[] { "committed", "used" }) {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isMemoryPollingEnabled();
		}
		
		public File getTemplateFile() throws IOException {
			return new ClassPathResource("template/memory-default-object.xml").getFile();
		}
	}, 
	THREAD_COUNT("java.lang:type=Threading", "ThreadCount", null) {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isThreadPollingEnabled();
		}
		
		public File getTemplateFile() throws IOException {
			return new ClassPathResource("template/thread-default-object.xml").getFile();
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
	public abstract File getTemplateFile() throws IOException;
	
	public JmxObjectName handleObject(ApplicationConfig config, JmxAccessor jmxAccessor, JmxAttributeRepository repository) throws MalformedObjectNameException, 
			InstanceNotFoundException, IntrospectionException, AttributeNotFoundException, ReflectionException, MBeanException, IOException {
		
		JmxObjectName jmxObjectName = repository.getJmxObjectNameWithAttributes(objectName);
		
		if (isEnabled(config)) {
			if (jmxObjectName == null) {
				// Option is enabled and object/attribute do not exist - add them
				jmxObjectName = repository.insertJmxObjectName(jmxAccessor.lookupAttribute(objectName, attribute, attributePaths)); // Reassigning so that we have all the primary keys
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
