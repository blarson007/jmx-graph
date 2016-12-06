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

import com.jmxgraph.config.SingletonManager;
import com.jmxgraph.domain.JmxAttribute;
import com.jmxgraph.domain.JmxObjectName;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.JmxAttributeRepository;


@WebServlet(name = "ObjectNameSelection", urlPatterns = { "/object-name-selection.html" })
public class ObjectNameSelectionServlet extends HttpServlet {

	private static final long serialVersionUID = 478306846374934360L;
	private static final Logger logger = LoggerFactory.getLogger(ObjectNameSelectionServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/jmx-object-selection.jsp");
		
		JmxAttributeRepository repository = SingletonManager.getJmxAttributeRepository();
		JmxAccessor jmxAccessor = SingletonManager.getJmxAccessor();
		
		String selectedObjectName = request.getParameter("objectName");
		if (selectedObjectName != null) { // Service the AJAX call
//			try {
//				Set<JmxAttributePath> paths = jmxAccessor.getAttributePathsForObjectName(selectedObjectName);
//			
//				String jsonResponse = new Gson().toJson(paths);
//				System.out.println(jsonResponse);
//				
//				response.setContentType("application/json");
//				response.getWriter().write(jsonResponse);
//			} catch (Exception e) {
//				logger.error("", e);
//			}	
				
		} else {
			Collection<JmxObjectName> jmxList = repository.getAllEnabledAttributePaths();
			request.setAttribute("jmxList", jmxList);
			
			try {
				Set<JmxObjectName> objectNames = jmxAccessor.getAllAvailableObjectsWithAttributes();
				request.setAttribute("objectNameMap", buildObjectNameMap(objectNames));
			} catch (Exception e) {
				logger.error("", e);
			}
			
			dispatcher.forward(request, response);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String objectName = request.getParameter("objectName");
		String attributeName = request.getParameter("attributeName");
		String attributeType = request.getParameter("attributeType");
		
		JmxAttributeRepository repository = SingletonManager.getJmxAttributeRepository();
		
		JmxObjectName jmxObjectName = null;
		JmxAttribute jmxAttribute = null;
		try {
			jmxObjectName = repository.getJmxObjectName(objectName);
			jmxAttribute = repository.getJmxAttribute(jmxObjectName.getObjectNameId(), attributeName);
			
			if (!jmxAttribute.isEnabled()) { // Enable the attribute path
				logger.debug("Attribute " + attributeName + " is not enabled. Enabling attribute.");
				repository.enableJmxAttributePath(jmxAttribute.getAttributeId());
			} else { // Disable the attribute path
				logger.debug("Attribute " + attributeName + " is already enabled. Disabling attribute.");
				repository.disableJmxAttributePath(jmxAttribute.getAttributeId());
			}
		} catch (EmptyResultDataAccessException e) {
			logger.debug("Attribute " + attributeName + " was not found in the database. Persisting attribute.");
//			repository.insertJmxAttributePath(new JmxAttributePath(objectName, attribute, attributeType));
			if (jmxObjectName == null) {
				Set<JmxAttribute> attributes = new HashSet<>();
				JmxAttribute attributeToInsert = new JmxAttribute(attributeName, attributeType);
				attributes.add(attributeToInsert);
				
				repository.insertJmxObjectName(new JmxObjectName(objectName, "", attributes));
			} else {
				repository.insertJmxAttribute(jmxObjectName.getObjectNameId(), new JmxAttribute(attributeName, attributeType));
			}
		}
	}
	
	private Map<String, Set<JmxObjectName>> buildObjectNameMap(Set<JmxObjectName> objectNames) {
		Map<String, Set<JmxObjectName>> objectNameMap = new HashMap<>();
		
		for (JmxObjectName objectName : objectNames) {
			String canonicalObjectName = objectName.getCanonicalName();
			String prefix = canonicalObjectName.contains(":") ? canonicalObjectName.split(":")[0] : canonicalObjectName;
			
			Set<JmxObjectName> canonicalNames = objectNameMap.get(prefix);
			if (canonicalNames == null) {
				canonicalNames = new HashSet<>();
				objectNameMap.put(prefix, canonicalNames);
			}
			
			canonicalNames.add(objectName);
		}
		
		return objectNameMap;
	}
}
