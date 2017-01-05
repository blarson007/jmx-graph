package com.jmxgraph.mbean;

import java.io.IOException;
import java.util.Collection;
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
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.ServiceUnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.config.Initializable;
import com.jmxgraph.domain.appconfig.JmxConnectionConfig;
import com.jmxgraph.domain.jmx.JmxAttribute;
import com.jmxgraph.domain.jmx.JmxObjectName;
import com.jmxgraph.ui.JmxTestResult;
import com.jmxgraph.ui.JmxTestResult.StatusCode;

public class JmxAccessor implements Initializable<JmxConnectionConfig> {
	
	private static final Logger logger = LoggerFactory.getLogger(JmxAccessor.class);
	
	private JMXConnector jmxConnector;
	private MBeanServerConnection mBeanServerConnection;
	
	private JmxAccessor() {  }
	
	private static class InstanceHolder {
		private static final JmxAccessor instance = new JmxAccessor();
	}
	
	public static JmxAccessor getInstance() {
		return InstanceHolder.instance;
	}
	
	@Override
	public boolean isInitialized() {
		return jmxConnector != null && mBeanServerConnection != null;
	}
	
	public void initialize(JmxConnectionConfig config, boolean registerShutdownHook) throws Exception {
		JMXServiceURL serviceUrl = new JMXServiceURL(buildJmxUrl(config.getJmxHost(), config.getJmxPort()));
		Map<String, String[]> env = getConnectionEnv(config.getJmxUsername(), config.getJmxPassword());
		
		jmxConnector = JMXConnectorFactory.connect(serviceUrl, env);
		mBeanServerConnection = jmxConnector.getMBeanServerConnection();
		
		if (registerShutdownHook) {
			registerShutdownHook(this);
		}	
	}

	@Override
	public void initialize(JmxConnectionConfig config) throws Exception {
		initialize(config, true);
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
	
	public JmxObjectName lookupAttribute(String objectName, String attribute, Collection<String> paths) throws MalformedObjectNameException, InstanceNotFoundException, 
			IntrospectionException, ReflectionException, IOException, AttributeNotFoundException, MBeanException {
		String[] pathArray = null;
		if (paths != null) {
			pathArray = new String[paths.size()];
			int index = 0;
			for (String path : paths) {
				pathArray[index] = path;
				index++;
			}
		}
		return lookupAttribute(objectName, attribute, pathArray);
	}
	
	public JmxObjectName lookupAttribute(String objectName, String attribute, String... paths) throws MalformedObjectNameException, InstanceNotFoundException, 
			IntrospectionException, ReflectionException, IOException, AttributeNotFoundException, MBeanException {
		Set<JmxAttribute> attributes = new HashSet<>();
		
		ObjectName mbeanObjectName = new ObjectName(objectName);
		MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(mbeanObjectName);
		
		for (MBeanAttributeInfo attributeInfo : mBeanInfo.getAttributes()) {
			if (attributeInfo.getName().equals(attribute)) {
				if (paths != null && paths.length > 0 && attributeInfo.getType().equalsIgnoreCase("javax.management.openmbean.CompositeData")) {
					CompositeDataSupport attributeValue = (CompositeDataSupport) getAttributeValue(objectName, attributeInfo.getName());
					
					for (String path : paths) {
						attributes.add(new JmxAttribute(attributeInfo.getName(), attributeValue.getCompositeType().getType(path).getTypeName(), path));
					}
				} else {
					attributes.add(new JmxAttribute(attributeInfo.getName(), attributeInfo.getType()));
				}
				break;
			}
		}
		
		return new JmxObjectName(mbeanObjectName.getCanonicalName(), mBeanInfo.getDescription(), attributes);
	}
	
	public void shutdown() {
		try {
			logger.warn("Closing jmx connection");
			jmxConnector.close();
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	private String buildJmxUrl(String host, int port) {
		return "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";
	}
	
	private Map<String, String[]> getConnectionEnv(String jmxUsername, String jmxPassword) {
		Map<String, String[]> env = null;
		if (jmxUsername != null && jmxPassword != null) {
			env = new HashMap<>();
			env.put(JMXConnector.CREDENTIALS, new String[] { jmxUsername, jmxPassword });
		}
		return env;
	}
	
	private void registerShutdownHook(final JmxAccessor jmxAccessor) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				jmxAccessor.shutdown();
			}	
		});
	}
	
	public static JmxTestResult testJmxConnection(JmxConnectionConfig config) {
		JmxAccessor jmxAccessor = null;
		try {
			jmxAccessor = new JmxAccessor();
			jmxAccessor.initialize(config, false);
			return new JmxTestResult(StatusCode.SUCCESS, "The JMX Connection info is correct");
		} catch (ServiceUnavailableException e) {
			return new JmxTestResult(StatusCode.FAILURE, "Unable to connect. Verify that the JMX Host and JMX Port are entered correctly.");
		} catch (Exception e) {
			return new JmxTestResult(StatusCode.FAILURE, e.getLocalizedMessage());
		} finally {
			if (jmxAccessor != null) {
				jmxAccessor.shutdown();
			}
		}
	}
	
	// This is how we would search for all object names that belong to com.jamfsoftware
	//	Set<ObjectName> names = mbeanConn.queryNames(new ObjectName("com.jamfsoftware:*"), null);
}
