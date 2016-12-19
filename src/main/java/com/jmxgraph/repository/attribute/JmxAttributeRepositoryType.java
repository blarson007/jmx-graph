package com.jmxgraph.repository.attribute;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.hsqldb.Server;
import org.hsqldb.util.DatabaseManagerSwing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public enum JmxAttributeRepositoryType {
	
	IN_MEMORY_DB {
		@Override
		public void createRepository() {
			logger.warn("Setting up in-memory datasource");
			
			EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder()
					.setType(EmbeddedDatabaseType.HSQL)
					.addScript("sql/create-db.sql").build();
			
			HikariDataSource hikariDataSource = new HikariDataSource();
			hikariDataSource.setDataSource(embeddedDatabase);
			hikariDataSource.setMinimumIdle(5);
			hikariDataSource.setMaximumPoolSize(5);
			
			logger.warn("Registering database shutdown hook");
			registerShutdownHook(embeddedDatabase);
			registerShutdownHook(hikariDataSource);
			
			DatabaseManagerSwing.main(new String[] { "--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", "" });
			
			JdbcAttributeRepository.getInstance().initialize(hikariDataSource);
		}
	},
	
	EMBEDDED_DB {
		@Override
		public void createRepository() {
			logger.warn("Setting up embedded datasource");
			
			boolean running = false;
			
			Server server = new Server();
			server.setDatabasePath(0, "file:" + System.getProperty("user.home") + "/.jmxgraph/db");
			server.setPort(9001);
			server.setDatabaseName(0, "jmx");
			server.checkRunning(running);
			
			if (!running) {
				server.start();
				registerShutdownHook(server);
			}
			
			HikariConfig hikariConfig = new HikariConfig();
			hikariConfig.setDriverClassName(org.hsqldb.jdbcDriver.class.getName());
			hikariConfig.setJdbcUrl("jdbc:hsqldb:hsql://localhost:9001/jmx");
			hikariConfig.setUsername("sa");
			hikariConfig.setMinimumIdle(5);
			hikariConfig.setMaximumPoolSize(5);
			
			HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
			buildTables(hikariDataSource);
			
			DatabaseManagerSwing.main(new String[] { "--url", "jdbc:hsqldb:hsql://localhost:9001/jmx", "--user", "sa", "--password", "" });
			
			JdbcAttributeRepository.getInstance().initialize(hikariDataSource);
		}
	};
	
	private static final Logger logger = LoggerFactory.getLogger(JmxAttributeRepositoryType.class);
	
	public abstract void createRepository();
	
	private static void registerShutdownHook(final HikariDataSource hikariDataSource) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.warn("Shutting down datasource");
				hikariDataSource.close();
			}
		});
	}
	
	private static void registerShutdownHook(final EmbeddedDatabase embeddedDatabase) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.warn("Shutting down in-memory database");
				embeddedDatabase.shutdown();
			}
		});
	}
	
	private static void registerShutdownHook(final Server server) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.warn("Shutting down embedded server");
				server.shutdown();
			}
		});
	}
	
	private static void buildTables(DataSource dataSource) {
		// TODO: This may have to become very elaborate in the future; Basically compare the existing schema with
		// the future schema and make all 'upgrade' modifications. Is there a way to avoid this?
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<String> tables = jdbcTemplate.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLES where table_type = 'TABLE'", String.class); 
		
		if (tables.size() == 0) {
			logger.warn("Tables not found. Building tables.");
			try (Connection connection = dataSource.getConnection()) {
				ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/create-db.sql"));
			} catch (Exception e) {
				logger.error("", e);
			}
		} else {
			logger.warn("Tables already exist. Skipping table build.");
		}
	}
}
