package com.jmxgraph.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jmxgraph.businessaction.PollScheduler;

public enum GraphFilter {

	NOW(1, 10, "Now", "h:mm:ss") {
		@Override
		public Date getSqlDateClause() {
			Calendar now = Calendar.getInstance();
			int numberOfSeconds = (10 * PollScheduler.getInstance().getPollIntervalInSeconds()) + 1;
			now.add(Calendar.SECOND, -numberOfSeconds);
			return now.getTime();
		}
	},
	LAST_10_MINUTES(2, 600, "Last 10 Minutes", "h:mm"),
	LAST_HOUR(3, 3600, "Last Hour", "h:mm"),
	LAST_DAY(4, 86400, "Last Day", "h:mm a");
	
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
	
	public Date getSqlDateClause() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, -numberOfSeconds);
		return now.getTime();
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
