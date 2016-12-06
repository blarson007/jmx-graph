package com.jmxgraph.config;

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
        
        tomcat.start();
//        tomcat.getServer().await();
	}
}
