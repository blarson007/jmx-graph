package com.jmxgraph.config;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.apache.commons.cli.ParseException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.jmxgraph.client.JmxClientJob;
import com.jmxgraph.util.Initializable;


public class PollScheduler implements Initializable<Long> {
	
	private static final long DEFAULT_POLL_INTERVAL_IN_SECONDS = 5;

	private long pollIntervalInSeconds;
	private Date initializedOn;
	
	private static PollScheduler instance = null;
	
	public static PollScheduler getInstance() {
		synchronized(instance) {
			if (instance == null) {
				instance = new PollScheduler();
			}
			return instance;
		}
	}
	
	public PollScheduler() {
		pollIntervalInSeconds = DEFAULT_POLL_INTERVAL_IN_SECONDS;
	}
	
	public boolean isInitialized() {
		return initializedOn != null && initializedOn.before(new Date());
	}
	
	public void initialize(Long pollIntervalInSeconds) {
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
	    
		initializedOn = scheduler.scheduleJob(jobDetail, trigger);
	}
	
	public long getPollIntervalInSeconds() {
		return pollIntervalInSeconds;
	}
}
