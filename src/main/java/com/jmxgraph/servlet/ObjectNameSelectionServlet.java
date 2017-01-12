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

import com.jmxgraph.businessaction.JmxGraphHandler;
import com.jmxgraph.domain.jmx.JmxObjectName;
import com.jmxgraph.mbean.JmxAccessor;
import com.jmxgraph.repository.jmx.JdbcAttributeRepository;
import com.jmxgraph.repository.jmx.JmxAttributeRepository;


@WebServlet(name = "ObjectNameSelection", urlPatterns = { "/object-name-selection.html" })
public class ObjectNameSelectionServlet extends HttpServlet {

	private static final long serialVersionUID = 478306846374934360L;
	private static final Logger logger = LoggerFactory.getLogger(ObjectNameSelectionServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/jmx-object-selection.jsp");
		
		JmxAttributeRepository repository = JdbcAttributeRepository.getInstance();
		JmxAccessor jmxAccessor = JmxAccessor.getInstance();
		
		if (jmxAccessor.isInitialized()) {
			request.setAttribute("jmxConfigured", true);
			
			Collection<JmxObjectName> jmxList = repository.getAllEnabledAttributePaths();
			request.setAttribute("jmxList", jmxList);
			
			try {
				Set<JmxObjectName> objectNames = jmxAccessor.getAllAvailableObjectsWithAttributes();
				request.setAttribute("objectNameMap", buildObjectNameMap(objectNames));
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		
		dispatcher.forward(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String objectName = request.getParameter("objectName");
		String attributeName = request.getParameter("attributeName");
		String attributeType = request.getParameter("attributeType");
		
		JmxGraphHandler.getInstance().toggleJmxGraph(objectName, attributeName, attributeType);
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
