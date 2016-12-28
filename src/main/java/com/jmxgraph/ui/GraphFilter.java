package com.jmxgraph.ui;

import java.text.SimpleDateFormat;

import com.jmxgraph.businessaction.PollScheduler;

public enum GraphFilter {

	NOW(1, 10, "Now", "hh:mm:ss") {
		@Override
		public int getSqlLimit() {
			return 10;
		}
	},
	LAST_10_MINUTES(2, 600, "Last 10 Minutes", "hh:mm"),
	LAST_HOUR(3, 3600, "Last Hour", "hh:mm"),
	LAST_DAY(4, 86400, "Last Day", "hh:mm");
	
	private int filterId;
	private int numberOfSeconds;
	private String description;
	private SimpleDateFormat labelFormat;
	
	GraphFilter(int filterId, int numberOfSeconds, String description, String labelFormat) {
		this.filterId = filterId;
		this.numberOfSeconds = numberOfSeconds;
		this.description = description;
		this.labelFormat = new SimpleDateFormat(labelFormat);
	}
	
	public int getFilterId() {
		return filterId;
	}
	
	public long getNumberOfSeconds() {
		return numberOfSeconds;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getLabelFormat() {
		return labelFormat.toPattern();
	}
	
	public int getSqlLimit() {
		int pollIntervalInSeconds = PollScheduler.getInstance().getPollIntervalInSeconds();
		return numberOfSeconds / pollIntervalInSeconds;
	}
	
	public static GraphFilter getFilterById(int filterId) {
		for (GraphFilter filter : values()) {
			if (filter.getFilterId() == filterId) {
				return filter;
			}
		}
		return null;
	}
}
