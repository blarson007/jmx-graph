package com.jmxgraph;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.config.JmxConfig;
import com.jmxgraph.config.PollScheduler;
import com.jmxgraph.config.TomcatManager;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.JdbcAttributeRepository;
import com.jmxgraph.repository.JmxAttributeRepositoryType;


public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine = null;

		try {
			commandLine = parser.parse(options, args);
			
			createAndRegisterRepository(commandLine);
			createAndRegisterJmxAccessor(commandLine);
			createAndRegisterPollScheduler(commandLine);
			
			TomcatManager.startTomcat();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private static Options createOptions() {
		Options options = new Options();

		options.addOption(Option.builder("host").longOpt("jmx-host").hasArg().desc("Required. The hostname of the JSS web application.").type(String.class).required().build());
		options.addOption(Option.builder("port").longOpt("jmx-port").hasArg().desc("Required. The port that the JSS has exposed for consumption via JMX.").type(Integer.class).required().build());
		options.addOption(Option.builder("user").longOpt("jmx-user").hasArg().desc("Optional. The username that the JSS has exposed for consumption via JMX.").type(String.class).build());
		options.addOption(Option.builder("pass").longOpt("jmx-password").hasArg().desc("Optional. The password that is required for consumption via JMX.").type(String.class).build());
		options.addOption(Option.builder("int").longOpt("jmx-poll-interval-sec").hasArg().desc("Optional. The interval (in seconds) that the JMX collector should run.").type(Long.class).build());
		options.addOption(Option.builder("per").longOpt("persistence-type").hasArg().desc("Optional. Persistence type to use. Valid options are in-memory and database.").type(String.class).build());

		return options;
	}
	
	private static void createAndRegisterRepository(CommandLine commandLine) {
		JmxAttributeRepositoryType repositoryType = 
		commandLine.getOptionValue("per") != null && commandLine.getOptionValue("per").equalsIgnoreCase("database") ? JmxAttributeRepositoryType.EMBEDDED_DB : JmxAttributeRepositoryType.IN_MEMORY_DB;
		DataSource dataSource = repositoryType.createDataSource();
		
		JdbcAttributeRepository.getInstance().initialize(dataSource);
	}
	
	private static void createAndRegisterJmxAccessor(CommandLine commandLine) throws NumberFormatException, IOException {
		JmxConfig jmxConfig = new JmxConfig(
				commandLine.getOptionValue("host"), 
				Integer.parseInt(commandLine.getOptionValue("port")), 
				commandLine.getOptionValue("user"),
				commandLine.getOptionValue("pass")
		);
		
		JmxAccessor.getInstance().initialize(jmxConfig);
	}
	
	private static void createAndRegisterPollScheduler(CommandLine commandLine) throws SchedulerException, ParseException {
		String pollIntervalInSeconds = commandLine.getOptionValue("jmx-poll-interval-sec");
		PollScheduler pollScheduler = PollScheduler.getInstance();
		if (pollIntervalInSeconds != null) {
			pollScheduler.initialize(Long.parseLong(pollIntervalInSeconds));
		}
		pollScheduler.scheduleJobExecution();
	}
}
