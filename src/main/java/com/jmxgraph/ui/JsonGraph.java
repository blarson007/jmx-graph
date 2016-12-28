package com.jmxgraph.ui;

public class JsonGraph {

	private GraphObject graphObject;
	private String errorMessage;
	private String dateFormat;
	
	public JsonGraph(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public JsonGraph(GraphObject graphObject, String dateFormat) {
		this.graphObject = graphObject;
		this.dateFormat = dateFormat;
	}
	
	public GraphObject getGraphObject() {
		return graphObject;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}
}
