package com.jmxgraph.servlet;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jmxgraph.businessaction.PollScheduler;
import com.jmxgraph.domain.JmxObjectName;
import com.jmxgraph.repository.attribute.JdbcAttributeRepository;
import com.jmxgraph.repository.attribute.JmxAttributeRepository;
import com.jmxgraph.ui.GraphFilter;

@WebServlet(name = "JmxAttributeGraphServlet", urlPatterns = { "/jmx-attribute-graph.html" })
public class JmxAttributeGraphServlet extends HttpServlet {

	private static final long serialVersionUID = 2492396971460048886L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/jmx-mbean-graph.jsp");
		
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		PollScheduler pollScheduler = PollScheduler.getInstance();
		
		if (pollScheduler.isInitialized()) {
			request.setAttribute("jmxConfigured", true);
			
			Collection<JmxObjectName> enabledAttributePaths = repository.getAllEnabledAttributePaths();
			
			if (!enabledAttributePaths.isEmpty()) {
				request.setAttribute("jmxObjectsSubscribed", true);
			}
			
			request.setAttribute("pollIntervalMs", pollScheduler.getPollIntervalInSeconds() * 1000);
			request.setAttribute("filters", GraphFilter.values());
			request.setAttribute("jmxList", enabledAttributePaths);
		}
		
		dispatcher.forward(request, response);
	}
}
