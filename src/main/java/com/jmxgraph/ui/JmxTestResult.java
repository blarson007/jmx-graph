package com.jmxgraph.ui;

public class JmxTestResult {

	public enum StatusCode {
		SUCCESS, FAILURE;
	}
	
	private StatusCode statusCode;
	private String description;
	
	public JmxTestResult(StatusCode statusCode, String description) {
		this.statusCode = statusCode;
		this.description = description;
	}

	public StatusCode getStatusCode() {
		return statusCode;
	}

	public String getDescription() {
		return description;
	}
}
