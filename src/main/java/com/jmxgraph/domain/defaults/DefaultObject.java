package com.jmxgraph.domain.defaults;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;

import com.jmxgraph.domain.appconfig.ApplicationConfig;

public enum DefaultObject {

	PROCESS_CPU_LOAD {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isCpuPollingEnabled();
		}
		
		public InputStream getTemplateFile() throws IOException {
			return new ClassPathResource("template/cpu-default-object.xml").getInputStream();
		}
	},
	HEAP_MEMORY_USAGE {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isMemoryPollingEnabled();
		}
		
		public InputStream getTemplateFile() throws IOException {
			return new ClassPathResource("template/memory-default-object.xml").getInputStream();
		}
	}, 
	THREAD_COUNT {
		public boolean isEnabled(ApplicationConfig config) {
			return config.isThreadPollingEnabled();
		}
		
		public InputStream getTemplateFile() throws IOException {
			return new ClassPathResource("template/thread-default-object.xml").getInputStream();
		}
	};
	
	public abstract boolean isEnabled(ApplicationConfig config);
	public abstract InputStream getTemplateFile() throws IOException;
}
