package com.jmxgraph.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jmxgraph.businessaction.ApplicationConfigHandler;
import com.jmxgraph.domain.ApplicationConfig;
import com.jmxgraph.domain.JmxConnectionConfig;

@WebServlet(name = "ApplicationConfigServlet", urlPatterns = { "/configuration.html" })
public class ApplicationConfigServlet extends HttpServlet {
	
	private static final long serialVersionUID = -614029019862977794L;
	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfigServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/app-configuration.jsp");
		ApplicationConfigHandler applicationConfigHandler = ApplicationConfigHandler.getInstance();
		
		ApplicationConfig config = applicationConfigHandler.getExistingApplicationConfig();
		request.setAttribute("config", config);
		
		dispatcher.forward(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		JmxConnectionConfig jmxConnectionConfig = new JmxConnectionConfig();
		
		jmxConnectionConfig.setJmxHost(request.getParameter("jmxHost"));
		jmxConnectionConfig.setJmxPort(Integer.parseInt(request.getParameter("jmxPort")));
		jmxConnectionConfig.setJmxUsername(request.getParameter("jmxUsername"));
		jmxConnectionConfig.setJmxPassword(request.getParameter("jmxPassword"));
		
		applicationConfig.setJmxConnectionConfig(jmxConnectionConfig);
//		applicationConfig.setRepositoryType(JmxAttributeRepositoryType.valueOf(request.getParameter("repositoryType")));
		applicationConfig.setPollIntervalInSeconds(Integer.parseInt(request.getParameter("pollIntervalInSeconds")));
		
		try {
			boolean errors = false;
			if (StringUtils.isBlank(jmxConnectionConfig.getJmxHost())) {
				request.setAttribute("jmxHostError", "JMX Host is required.");
				errors = true;
			}
			
			if (jmxConnectionConfig.getJmxPort() == null || jmxConnectionConfig.getJmxPort() < 1) {
				request.setAttribute("jmxPortError", "JMX Port is required");
				errors = true;
			}
			
			if (errors) {
				getServletContext().getRequestDispatcher("/jsp/app-configuration.jsp").forward(request, response);
			}
			
			logger.warn("Applying application config updates");
			
			ApplicationConfigHandler applicationConfigHandler = ApplicationConfigHandler.getInstance();
			applicationConfigHandler.initialize(applicationConfig);
		} catch (Exception e) {
			logger.error("", e);
			request.setAttribute("config", applicationConfig);
		}
		
		response.sendRedirect("configuration.html");
	}
}
