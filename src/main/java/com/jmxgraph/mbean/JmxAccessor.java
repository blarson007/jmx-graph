package com.jmxgraph.mbean;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.domain.JmxAttribute;
import com.jmxgraph.domain.JmxObjectName;


public class JmxAccessor {
	
	private static final Logger logger = LoggerFactory.getLogger(JmxAccessor.class); 
	
	private MBeanServerConnection mBeanServerConnection;

	public JmxAccessor(String jmxHost, int jmxPort, String username, String password) throws IOException {
		JMXServiceURL serviceUrl = new JMXServiceURL(buildJmxUrl(jmxHost, jmxPort));
		
		Map<String, String[]> env = null;
		if (username != null && password != null) {
			env = new HashMap<>();
			env.put(JMXConnector.CREDENTIALS, new String[] { username, password });
		}	
		
		final JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceUrl, env);
		mBeanServerConnection = jmxConnector.getMBeanServerConnection();
		
		registerShutdownHook(jmxConnector);
	}
	
	public Object getAttributeValue(String objectName, String attribute) throws MalformedObjectNameException, 
			AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		return mBeanServerConnection.getAttribute(new ObjectName(objectName), attribute);
	}
	
	public Set<JmxObjectName> getAllAvailableObjectsWithAttributes() throws IOException, InstanceNotFoundException, IntrospectionException, ReflectionException {
		Set<JmxObjectName> objectNames = new HashSet<>();
		Set<ObjectName> names = mBeanServerConnection.queryNames(null, null);
		
		for (ObjectName objectName : names) {
			final MBeanInfo info = mBeanServerConnection.getMBeanInfo(objectName);
			
			Set<JmxAttribute> attributes = new HashSet<>();
			for (MBeanAttributeInfo attributeInfo : info.getAttributes()) {
				attributes.add(new JmxAttribute(attributeInfo.getName(), attributeInfo.getType()));
			}
			
			objectNames.add(new JmxObjectName(objectName.getCanonicalName(), info.getDescription(), attributes));
		}
		
		return objectNames;
	}
	
//	public Set<JmxAttributePath> getAttributePathsForObjectName(String canonicalObjectName) throws MalformedObjectNameException, 
//			InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
//		Set<JmxAttributePath> attributePaths = new HashSet<>();
//		ObjectName objectName = new ObjectName(canonicalObjectName);
//		
//		final MBeanInfo info = mBeanServerConnection.getMBeanInfo(objectName);
//		for (MBeanAttributeInfo attributeInfo : info.getAttributes()) {
//			attributePaths.add(new JmxAttributePath(objectName.getCanonicalName(), attributeInfo.getName(), attributeInfo.getType()));
//		}
//		
//		return attributePaths;
//	}
	
	private String buildJmxUrl(String host, int port) {
		return "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";
	}
	
	private void registerShutdownHook(final JMXConnector jmxConnector) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					logger.warn("Closing jmx connection");
					jmxConnector.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}	
		});
	}
	
	// This is how we would search for all object names that belong to com.jamfsoftware
	//	Set<ObjectName> names = mbeanConn.queryNames(new ObjectName("com.jamfsoftware:*"), null);
}
