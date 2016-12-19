package com.jmxgraph.businessaction;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class TomcatManager {

	public static void startTomcat() throws ServletException, LifecycleException {
		String contextPath = "/";
        String appBase = ".";
        
        Tomcat tomcat = new Tomcat();     
        tomcat.setPort(8081);
        tomcat.getHost().setAppBase(appBase);
        tomcat.addWebapp(contextPath, appBase);
        
        try {
	        Logger logger = Logger.getLogger("");
	        Handler fileHandler = new FileHandler("catalina.out", true);
	        fileHandler.setFormatter(new SimpleFormatter());
	        fileHandler.setLevel(Level.INFO);
	        fileHandler.setEncoding("UTF-8");
	        logger.addHandler(fileHandler);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        tomcat.start();
//        tomcat.getServer().await();
	}
}
