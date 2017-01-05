package com.jmxgraph.ui;

import java.util.Date;

public class GraphObject {

	private Series[] series;
	
	public GraphObject(Series... series) {
		this.series = series;
	}
	
	public Series[] getSeries() {
		return series;
	}

	public static class Series {
		private DataPoint[] data;
		
		public Series(DataPoint[] data) {
			this.data = data;
		}

		public DataPoint[] getData() {
			return data;
		}
	}
	
	public static class DataPoint {
		private Date x;
		private Object y;
		
		public DataPoint(Date x, Object y) {
			this.x = x;
			this.y = y;
		}
		
		public Date getX() {
			return x;
		}
		
		public Object getY() {
			return y;
		}
	}
}
