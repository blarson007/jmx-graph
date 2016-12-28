package com.jmxgraph.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmxgraph.domain.JmxAttribute;
import com.jmxgraph.repository.attribute.JdbcAttributeRepository;
import com.jmxgraph.repository.attribute.JmxAttributeRepository;
import com.jmxgraph.ui.GraphFilter;

@WebServlet(name = "JmxAttributeGraphAjaxServlet", urlPatterns = { "/jmx-attribute-graph-ajax.html" })
public class JmxAttributeGraphAjaxServlet extends HttpServlet {

	private static final long serialVersionUID = 6731429154018155352L;
	
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		
		String attributeId = request.getParameter("attributeId");
		String filterId = request.getParameter("filterId");
		GraphFilter filter = filterId == null ? GraphFilter.NOW : GraphFilter.getFilterById(Integer.parseInt(filterId));
		
		JmxAttribute attribute = repository.getJmxAttributeValuesByAttributeId(Integer.parseInt(attributeId), filter);

		String jsonResponse = mapper.writeValueAsString(attribute.getGraphObject(filter));
		System.out.println(jsonResponse);
		
		response.setContentType("application/json");
		response.getWriter().write(jsonResponse);
	}
}
