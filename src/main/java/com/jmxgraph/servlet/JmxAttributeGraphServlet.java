package com.jmxgraph.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jmxgraph.config.SingletonManager;
import com.jmxgraph.repository.JmxAttributeRepository;
import com.jmxgraph.ui.GraphFilter;

@WebServlet(name = "JmxAttributeGraphServlet", urlPatterns = { "/jmx-attribute-selection.html" })
public class JmxAttributeGraphServlet extends HttpServlet {

	private static final long serialVersionUID = 2492396971460048886L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/jmx-mbean-graph.jsp");
		JmxAttributeRepository repository = SingletonManager.getJmxAttributeRepository();
		
		request.setAttribute("pollIntervalMs", SingletonManager.getPollScheduler().getPollIntervalInSeconds() * 1000);
		request.setAttribute("filters", GraphFilter.values());
		request.setAttribute("jmxList", repository.getAllEnabledAttributePaths());
		
		dispatcher.forward(request, response);
	}
}
