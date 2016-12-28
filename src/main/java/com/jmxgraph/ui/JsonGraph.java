package com.jmxgraph.ui;

public class JsonGraph {

	private GraphObject graphObject;
	private String errorMessage;
	private String dateFormat;
	private String graphType;
	private boolean onlyInteger;
	
	public JsonGraph(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public JsonGraph(GraphObject graphObject, String dateFormat, String graphType, boolean onlyInteger) {
		this.graphObject = graphObject;
		this.dateFormat = dateFormat;
		this.graphType = graphType;
		this.onlyInteger = onlyInteger;
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
	
	public String getGraphType() {
		return graphType;
	}
	
	public boolean isOnlyInteger() {
		return onlyInteger;
	}
}
