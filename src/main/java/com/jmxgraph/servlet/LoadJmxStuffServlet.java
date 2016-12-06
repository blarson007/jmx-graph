package com.jmxgraph.servlet;

import java.io.IOException;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.ReflectionException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jmxgraph.config.SingletonManager;
import com.jmxgraph.mbean.JmxAccessor;

@WebServlet(name = "LoadJmxStuffServlet", urlPatterns = { "/load-jmx.html" })
public class LoadJmxStuffServlet extends HttpServlet {

	private static final long serialVersionUID = 4729426916565221L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		
		JmxAccessor jmxAccessor = SingletonManager.getJmxAccessor();
		try {
			jmxAccessor.getAllAvailableObjectsWithAttributes();
		} catch (InstanceNotFoundException | IntrospectionException | ReflectionException e) {
			e.printStackTrace();
		}
		
		System.out.println("Loading JMX attributes took " + (System.currentTimeMillis() - startTime) + " ms.");
	}
}
