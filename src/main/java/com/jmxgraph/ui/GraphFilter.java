package com.jmxgraph.ui;

import com.jmxgraph.businessaction.PollScheduler;

public enum GraphFilter {

	NOW(1, -1, "Now") {
		@Override
		public long getSqlLimit() {
			return 10;
		}
	},
	LAST_10_MINUTES(2, 600, "Last 10 Minutes"),
	LAST_HOUR(3, 3600, "Last Hour"),
	LAST_DAY(4, 86400, "Last Day");
	
	private int filterId;
	private long numberOfSeconds;
	private String description;
	
	GraphFilter(int filterId, long numberOfSeconds, String description) {
		this.filterId = filterId;
		this.numberOfSeconds = numberOfSeconds;
		this.description = description;
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
	
	public long getSqlLimit() {
		long pollIntervalInSeconds = PollScheduler.getInstance().getPollIntervalInSeconds();
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
