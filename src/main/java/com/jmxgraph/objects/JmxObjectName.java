package com.jmxgraph.objects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Deprecated // Keeping this around for historical purposes
public class JmxObjectName {

	private String canonicalName;
	
	private Set<String> attributes = new HashSet<String>();
	
	public JmxObjectName(final String canonicalName) {
		this.canonicalName = canonicalName;
	}
	
	public JmxObjectName(final String canonicalName, String... attributes) {
		this.canonicalName = canonicalName;
		for (String attribute : attributes) {
			addAttribute(attribute);
		}
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public Set<String> getAttributes() {
		return attributes;
	}
	
	public void addAttribute(String attribute) {
		attributes.add(attribute);
	}
	
	public static Collection<JmxObjectName> getDefaultObjectNames() {
		Set<JmxObjectName> names = new HashSet<JmxObjectName>();
		
		// An ObjectName can be created using the canonical name of the JMX bean
		names.add(new JmxObjectName("com.jamfsoftware:Context=/,Type=ThreadPoolMBean,Name=com.jamfsoftware.jss.managementextensions.GeneralThreadPoolMBean", 
				"ActiveThreadCount", "CurrentPoolSize", "QueuedTaskCount"));
		names.add(new JmxObjectName("com.jamfsoftware:Context=/,Type=CounterMBean,Name=com.jamfsoftware.jss.managementextensions.LdapConnectionsInUseMBean", "Count"));
//		names.add(new JmxObjectName("Catalina:type=Valve,context=/,host=localhost,name=SemaphoreRedirectValve", "availablePermits", "permitDeniedCounter"));
		
		return names;
	}
}
