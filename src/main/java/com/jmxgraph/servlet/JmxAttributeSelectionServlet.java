package com.jmxgraph.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
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

@WebServlet(name = "JmxAttributeSelection", urlPatterns = { "/jmx-attribute-selection.html" })
public class JmxAttributeSelectionServlet extends HttpServlet {

	private static final long serialVersionUID = 2492396971460048886L;
	
	private Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/jmx-mbean-selection.jsp");
		JmxAttributeRepository repository = SingletonManager.getJmxAttributeRepository();
		
		String attributeId = request.getParameter("attributeId");
		if (attributeId != null) {
			String filterId = request.getParameter("filterId");
			GraphFilter filter = filterId == null ? GraphFilter.NOW : GraphFilter.getFilterById(Integer.parseInt(filterId));
			
			JmxAttribute attribute = repository.getJmxAttributeValuesByAttributeId(Integer.parseInt(attributeId), filter);

			String jsonResponse = gson.toJson(attribute.getGraphObject());
			System.out.println(jsonResponse);
			
			response.setContentType("application/json");
			response.getWriter().write(jsonResponse);
		} else {
			request.setAttribute("pollIntervalMs", SingletonManager.getPollScheduler().getPollIntervalInSeconds() * 1000);
			request.setAttribute("filters", GraphFilter.values());
			request.setAttribute("jmxList", repository.getAllEnabledAttributePaths());
			
			dispatcher.forward(request, response);
		}
	}
}
