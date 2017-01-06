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
import com.jmxgraph.domain.jmx.JmxGraph;
import com.jmxgraph.repository.jmx.JdbcAttributeRepository;
import com.jmxgraph.repository.jmx.JmxAttributeRepository;
import com.jmxgraph.ui.GraphFilter;

@WebServlet(name = "JmxGraphServlet", urlPatterns = { "/jmx-graph.html" })
public class JmxGraphServlet extends HttpServlet {

	private static final long serialVersionUID = 4667524598138572747L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/jmx-graph.jsp");
		
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		PollScheduler pollScheduler = PollScheduler.getInstance();
		
		if (pollScheduler.isInitialized()) {
			request.setAttribute("jmxConfigured", true);
			
			Collection<JmxGraph> enabledGraphs = repository.getAllEnabledGraphs();
			
			if (!enabledGraphs.isEmpty()) {
				request.setAttribute("jmxObjectsSubscribed", true);
			}
			
			request.setAttribute("pollIntervalMs", pollScheduler.getPollIntervalInSeconds() * 1000);
			request.setAttribute("filters", GraphFilter.values());
			request.setAttribute("jmxList", enabledGraphs);
			request.setAttribute("attributeColors", new String[] { "red", "blue", "green", "yellow" });
		}
		
		dispatcher.forward(request, response);
	}	
}
