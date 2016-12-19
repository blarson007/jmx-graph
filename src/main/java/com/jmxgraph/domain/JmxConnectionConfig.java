package com.jmxgraph.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.jmxgraph.domain.adapter.PasswordEncryptionAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class JmxConnectionConfig {
	
	public static final String JMX_HOST_KEY = "jmx-host";
	public static final String JMX_PORT_KEY = "jmx-port";
	public static final String JMX_USERNAME_KEY = "jmx-username";
	public static final String JMX_PASSWORD_KEY = "jmx-password";

	private String jmxHost;
	private Integer jmxPort;
	private String jmxUsername;
	
	@XmlJavaTypeAdapter(value = PasswordEncryptionAdapter.class)
	private String jmxPassword;
	
	public JmxConnectionConfig() {  }
	
	public JmxConnectionConfig(String jmxHost, Integer jmxPort, String jmxUsername, String jmxPassword) {
		this.jmxHost = jmxHost;
		this.jmxPort = jmxPort;
		this.jmxUsername = jmxUsername;
		this.jmxPassword = jmxPassword;
	}
	
	public String getJmxHost() {
		return jmxHost;
	}
	
	public void setJmxHost(String jmxHost) {
		this.jmxHost = jmxHost;
	}
	
	public Integer getJmxPort() {
		return jmxPort;
	}
	
	public void setJmxPort(Integer jmxPort) {
		this.jmxPort = jmxPort;
	}
	
	public String getJmxUsername() {
		return jmxUsername;
	}
	
	public void setJmxUsername(String jmxUsername) {
		this.jmxUsername = jmxUsername;
	}
	
	public String getJmxPassword() {
		return jmxPassword;
	}
	
	public void setJmxPassword(String jmxPassword) {
		this.jmxPassword = jmxPassword;
	}
}
