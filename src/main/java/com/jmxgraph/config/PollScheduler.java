package com.jmxgraph.config;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.apache.commons.cli.ParseException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.jmxgraph.client.JmxClientJob;


public class PollScheduler {
	
	private static final long DEFAULT_POLL_INTERVAL_IN_SECONDS = 5;

	private long pollIntervalInSeconds;
	
	public PollScheduler() {
		pollIntervalInSeconds = DEFAULT_POLL_INTERVAL_IN_SECONDS;
	}
	
	public PollScheduler(long pollIntervalInSeconds) {
		this.pollIntervalInSeconds = pollIntervalInSeconds;
	}
	
	public void scheduleJobExecution() throws SchedulerException, ParseException {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    Scheduler scheduler = schedulerFactory.getScheduler();
	    
	    scheduler.start();
	    
		JobDetail jobDetail = newJob()
				.withIdentity("jmx-job")
				.ofType(JmxClientJob.class)
				.build();
		
		Trigger trigger = newTrigger()
				.withIdentity("jmx-trigger")
				.withSchedule(simpleSchedule()
						.withIntervalInMilliseconds(pollIntervalInSeconds * 1000)
						.repeatForever())
				.build();
	    
		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	public long getPollIntervalInSeconds() {
		return pollIntervalInSeconds;
	}
}
