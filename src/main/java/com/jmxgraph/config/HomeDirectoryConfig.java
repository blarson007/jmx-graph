package com.jmxgraph.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HomeDirectoryConfig {

	public static final String HOME_DIRECTORY = System.getProperty("user.home") + File.separator + ".jmxgraph";
	
	public static void createHomeDirectoryIfNotExists() throws IOException {
		Files.createDirectories(Paths.get(HOME_DIRECTORY));
	}
}
