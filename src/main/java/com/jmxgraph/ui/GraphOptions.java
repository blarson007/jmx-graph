package com.jmxgraph.ui;

public class GraphOptions {
	
	private AxisX axisX;
	
	public GraphOptions(AxisX axisX) {
		this.axisX = axisX;
	}
	
	public AxisX getAxisX() {
		return axisX;
	}

	public static class AxisX {
		private String type;
		private int divisor;
		
		public AxisX(String type, int divisor) {
			this.type = type;
			this.divisor = divisor;
		}
		
		public String getType() {
			return type;
		}
		public int getDivisor() {
			return divisor;
		}
	}
}
