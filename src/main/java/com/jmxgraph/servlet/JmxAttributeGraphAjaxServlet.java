package com.jmxgraph.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.jmxgraph.config.SingletonManager;
import com.jmxgraph.domain.JmxAttribute;
import com.jmxgraph.repository.JmxAttributeRepository;
import com.jmxgraph.ui.GraphFilter;

@WebServlet(name = "JmxAttributeGraphAjaxServlet", urlPatterns = { "/jmx-attribute-graph-ajax.html" })
public class JmxAttributeGraphAjaxServlet extends HttpServlet {

	private static final long serialVersionUID = 6731429154018155352L;
	
	private Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JmxAttributeRepository repository = SingletonManager.getJmxAttributeRepository();
		
		String attributeId = request.getParameter("attributeId");
		String filterId = request.getParameter("filterId");
		GraphFilter filter = filterId == null ? GraphFilter.NOW : GraphFilter.getFilterById(Integer.parseInt(filterId));
		
		JmxAttribute attribute = repository.getJmxAttributeValuesByAttributeId(Integer.parseInt(attributeId), filter);

		String jsonResponse = gson.toJson(attribute.getGraphObject());
		System.out.println(jsonResponse);
		
		response.setContentType("application/json");
		response.getWriter().write(jsonResponse);
	}
}
