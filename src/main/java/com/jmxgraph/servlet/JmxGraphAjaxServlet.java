package com.jmxgraph.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmxgraph.businessaction.JmxGraphHandler;
import com.jmxgraph.ui.GraphFilter;
import com.jmxgraph.ui.JsonGraph;

@WebServlet(name = "JmxGraphAjaxServlet", urlPatterns = { "/jmx-graph-ajax.html" })
public class JmxGraphAjaxServlet extends HttpServlet {

	private static final long serialVersionUID = 3162588107897104947L;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String graphId = request.getParameter("graphId");
		String filterId = request.getParameter("filterId");
		GraphFilter filter = filterId == null ? GraphFilter.NOW : GraphFilter.getFilterById(Integer.parseInt(filterId));
		
		JsonGraph jsonGraph = JmxGraphHandler.getInstance().buildGraph(Integer.parseInt(graphId), filter);

		String jsonResponse = mapper.writeValueAsString(jsonGraph);
		System.out.println(jsonResponse);
		
		response.setContentType("application/json");
		response.getWriter().write(jsonResponse);
	}
}
