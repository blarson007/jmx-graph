package com.jmxgraph.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;

import com.google.gson.Gson;
import com.jmxgraph.domain.JmxAttributePath;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.JdbcAttributeRepository;
import com.jmxgraph.repository.JmxAttributeRepository;
import com.jmxgraph.ui.ObjectNameHolder;


@WebServlet(name = "ObjectNameSelection", urlPatterns = { "/object-name-selection.html" })
public class ObjectNameSelectionServlet extends HttpServlet {

	private static final long serialVersionUID = 478306846374934360L;
	private static final Logger logger = LoggerFactory.getLogger(ObjectNameSelectionServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/jmx-object-selection.jsp");
		
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		JmxAccessor jmxAccessor = JmxAccessor.getInstance();
		
		String selectedObjectName = request.getParameter("objectName");
		if (selectedObjectName != null) { // Service the AJAX call
			try {
				Set<JmxAttributePath> paths = jmxAccessor.getAttributePathsForObjectName(selectedObjectName);
			
				String jsonResponse = new Gson().toJson(paths);
				System.out.println(jsonResponse);
				
				response.setContentType("application/json");
				response.getWriter().write(jsonResponse);
			} catch (Exception e) {
				logger.error("", e);
			}	
				
		} else {
			Collection<JmxAttributePath> jmxList = repository.getAllEnabledAttributePaths();
			request.setAttribute("jmxList", jmxList);
			
			try {
				Set<String> attributePaths = jmxAccessor.getAllObjectNames();
				request.setAttribute("objectNameMap", buildObjectNameMap(attributePaths));
			} catch (Exception e) {
				logger.error("", e);
			}
			
			dispatcher.forward(request, response);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String objectName = request.getParameter("objectName");
		String attribute = request.getParameter("attribute");
		String attributeType = request.getParameter("attributeType");
		
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		try {
			JmxAttributePath jmxAttributePath = repository.getJmxAttributePath(objectName, attribute);
			
			if (!jmxAttributePath.isEnabled()) { // Enable the attribute path
				logger.debug("Attribute " + attribute + " is not enabled. Enabling attribute.");
				repository.enableJmxAttributePath(jmxAttributePath.getPathId());
			} else { // Disable the attribute path
				logger.debug("Attribute " + attribute + " is already enabled. Disabling attribute.");
				repository.disableJmxAttributePath(jmxAttributePath.getPathId());
			}
		} catch (EmptyResultDataAccessException e) {
			logger.debug("Attribute " + attribute + " was not found in the database. Persisting attribute.");
			repository.insertJmxAttributePath(new JmxAttributePath(objectName, attribute, attributeType));
		}
	}
	
	private Map<String, Set<ObjectNameHolder>> buildObjectNameMap(Set<String> objectNames) {
		Map<String, Set<ObjectNameHolder>> objectNameMap = new HashMap<>();
		
		for (String objectName : objectNames) {
			String prefix = objectName.contains(":") ? objectName.split(":")[0] : objectName;
			
			Set<ObjectNameHolder> canonicalNames = objectNameMap.get(prefix);
			if (canonicalNames == null) {
				canonicalNames = new HashSet<>();
				objectNameMap.put(prefix, canonicalNames);
			}
			
			canonicalNames.add(new ObjectNameHolder(objectName));
		}
		
		return objectNameMap;
	}
}
