package com.jmxgraph.ui;

public class GraphObject {

	private String[] labels;
	private Series[] series;
	private String errorMessage;
	
	public GraphObject(String[] labels, Series series) {
		this.labels = labels;
		this.series =  new Series[] { series };
//		this.axisX = new AxisX("Chartist.FixedScaleAxis", 10);
	}
	
	public GraphObject(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String[] getLabels() {
		return labels;
	}

	public Series[] getSeries() {
		return series;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public static class Series {
		private Object[] data;
		
		public Series(Object[] data) {
			this.data = data;
		}

		public Object[] getData() {
			return data;
		}
	}
}
