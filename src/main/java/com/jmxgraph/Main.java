package com.jmxgraph;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.businessaction.ApplicationConfigHandler;
import com.jmxgraph.businessaction.JmxTemplateHandler;
import com.jmxgraph.businessaction.TomcatManager;
import com.jmxgraph.config.HomeDirectoryConfig;
import com.jmxgraph.domain.appconfig.ApplicationConfig;
import com.jmxgraph.domain.appconfig.JmxConnectionConfig;
import com.jmxgraph.repository.jmx.JmxAttributeRepositoryType;


public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		Options options = createOptions();
		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine = null;

		ApplicationConfig config = null;
		try {
			HomeDirectoryConfig.createHomeDirectoryIfNotExists();
			
			commandLine = commandLineParser.parse(options, args);
			config = parseCommandLineConfig(commandLine);
			
			config.getRepositoryType().createRepository(commandLine.hasOption("db"));
		} catch (Exception e) {
			logger.error("Fatal error while starting up the application.", e);
			System.exit(1);
		}	
			
		try {
			ApplicationConfigHandler applicationConfigHandler = ApplicationConfigHandler.getInstance();
			
			if (config.deviatesFromDefault()) {
				// Let's try to connect to JMX using values from the command line
				applicationConfigHandler.initialize(config);
			} else {
				ApplicationConfig existingConfig = applicationConfigHandler.getExistingApplicationConfig();
				
				if (existingConfig != null && StringUtils.isNotEmpty(existingConfig.getJmxConnectionConfig().getJmxHost())) {
					// The application has already been configured. Let's use the existing configuration to start the application.
					applicationConfigHandler.initialize(existingConfig);
				}
			}
		} catch (Exception e) {
			logger.warn("Error while initializing the application", e);
		}	

		try {
			TomcatManager.startTomcat();
		} catch (Exception e) {
			logger.error("Fatal error while starting up Tomcat", e);
			System.exit(1);
		}
		
		String[] templateFilePaths = commandLine.getOptionValues("template");
		if (templateFilePaths != null && templateFilePaths.length > 0) {
			if (ApplicationConfigHandler.getInstance().isInitialized()) {
				// If the application has been started, we can attempt to apply the templates
				JmxTemplateHandler.getInstance().processJmxTemplates(templateFilePaths);
			} else {
				// If the application has not been started, we'll save the templates and try to apply them later
				JmxTemplateHandler.getInstance().saveJmxTemplates(templateFilePaths);
			}
		}	
	}

	private static Options createOptions() {
		Options options = new Options();

		options.addOption(Option.builder("host").longOpt(JmxConnectionConfig.JMX_HOST_KEY).hasArg().desc("The hostname of the web application.").type(String.class).build());
		options.addOption(Option.builder("port").longOpt(JmxConnectionConfig.JMX_PORT_KEY).hasArg().desc("The port that the web application has exposed for consumption via JMX.").type(Integer.class).build());
		options.addOption(Option.builder("user").longOpt(JmxConnectionConfig.JMX_USERNAME_KEY).hasArg().desc("The username that the web application has exposed for consumption via JMX.").type(String.class).build());
		options.addOption(Option.builder("pass").longOpt(JmxConnectionConfig.JMX_PASSWORD_KEY).hasArg().desc("The password that is required for consumption via JMX.").type(String.class).build());
		options.addOption(Option.builder("int").longOpt(ApplicationConfig.POLL_INTERVAL_KEY).hasArg().desc("The interval (in seconds) that the JMX collector should run.").type(Long.class).build());
		options.addOption(Option.builder("per").longOpt(ApplicationConfig.REPOSITORY_TYPE_KEY).hasArg().desc("Persistence type to use. Valid options are in-memory and database.").type(String.class).build());
		options.addOption(Option.builder("template").longOpt("jmx-template").hasArgs().desc("Paths to template files that contain graph object representations.").type(String.class).build());
		options.addOption(Option.builder("db").longOpt("database-viewer").hasArgs().desc("Enable the database manager for debug purposes.").type(Boolean.class).build());

		return options;
	}
	
	private static ApplicationConfig parseCommandLineConfig(CommandLine commandLine) {
		ApplicationConfig config = new ApplicationConfig();
		
		String persistence = commandLine.getOptionValue("per");
		JmxAttributeRepositoryType repositoryType = persistence != null && persistence.equalsIgnoreCase("in-memory") ? JmxAttributeRepositoryType.IN_MEMORY_DB : JmxAttributeRepositoryType.EMBEDDED_DB;
		config.setRepositoryType(repositoryType);
		
		try {
			String pollIntervalInSeconds = commandLine.getOptionValue("jmx-poll-interval-sec");
			if (pollIntervalInSeconds != null) {
				config.setPollIntervalInSeconds(Integer.parseInt(pollIntervalInSeconds));
			}
		} catch (Exception e) {
			logger.warn("Error while setting poll interval value: " +  commandLine.getOptionValue("jmx-poll-interval-sec") + ". Falling back on default.");
		}
		
		config.setJmxConnectionConfig(parseJmxConnectionConfig(commandLine));
		
		return config;
	}
	
	private static JmxConnectionConfig parseJmxConnectionConfig(CommandLine commandLine) {
		Integer jmxPort = null;
		try {
			String jmxPortAsString = commandLine.getOptionValue("port");
			if (jmxPortAsString != null) {
				jmxPort = Integer.parseInt(jmxPortAsString);
			}
		} catch (Exception e) {
			logger.warn("Error while setting JMX port value: " + commandLine.getOptionValue("port") + ". Falling back on default value NULL.");
		}
		
		return new JmxConnectionConfig(commandLine.getOptionValue("host"), jmxPort, commandLine.getOptionValue("user"), commandLine.getOptionValue("pass"));
	}
}
