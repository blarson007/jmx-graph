package com.jmxgraph.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmxgraph.domain.JmxConnectionConfig;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.ui.JmxTestResult;
import com.jmxgraph.ui.JmxTestResult.StatusCode;

@WebServlet(name = "ApplicationConfigAjaxServlet", urlPatterns = { "/configuration-ajax.html" })
public class ApplicationConfigAjaxServlet extends HttpServlet {

	private static final long serialVersionUID = 1879629425881037160L;
	
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jmxHost = request.getParameter("jmxHost");
		String jmxPortAsAString = request.getParameter("jmxPort");
		String jmxUsername = request.getParameter("jmxUsername");
		String jmxPassword = request.getParameter("jmxPassword");
		
		JmxTestResult result;
		if (StringUtils.isEmpty(jmxHost)) {
			result = new JmxTestResult(StatusCode.FAILURE, "Please enter a valid JMX host to test.");
		} else if (StringUtils.isEmpty(jmxPortAsAString)) {
			result = new JmxTestResult(StatusCode.FAILURE, "Please enter a valid JMX port to test.");
		} else {
			try {
				result = JmxAccessor.testJmxConnection(new JmxConnectionConfig(jmxHost, Integer.parseInt(jmxPortAsAString), jmxUsername, jmxPassword));
			} catch (NumberFormatException nfe) {
				result = new JmxTestResult(StatusCode.FAILURE, "The JMX port must be a number.");
			}
		}
		
		response.setContentType("application/json");
		response.getWriter().write(mapper.writeValueAsString(result));
	}
}
