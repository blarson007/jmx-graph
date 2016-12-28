package com.jmxgraph.businessaction;

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
import com.jmxgraph.config.Initializable;


public class PollScheduler implements Initializable<Integer> {
	
	private int pollIntervalInSeconds;
	private Scheduler scheduler;
	
	private PollScheduler() {  }
	
	private static class InstanceHolder {
		private static final PollScheduler instance = new PollScheduler();
	}
	
	public static PollScheduler getInstance() {
		return InstanceHolder.instance;
	}
	
	@Override
	public void initialize(Integer pollIntervalInSeconds) throws SchedulerException, ParseException {
		this.pollIntervalInSeconds = pollIntervalInSeconds;
		scheduleJobExecution();
	}
	
	@Override
	public boolean isInitialized() {
		try {
			return scheduler != null && scheduler.isStarted();
		} catch (SchedulerException e) {
			return false;
		}
	}
	
	private void scheduleJobExecution() throws SchedulerException, ParseException {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    scheduler = schedulerFactory.getScheduler();
	    
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
	
	public int getPollIntervalInSeconds() {
		return pollIntervalInSeconds;
	}
	
	public void stopJobExection() throws SchedulerException {
		scheduler.shutdown(true);
	}
}
