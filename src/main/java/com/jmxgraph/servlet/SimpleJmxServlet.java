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


@WebServlet(name = "SimpleJmxServlet", urlPatterns = { "/simple-jmx.html" })
public class SimpleJmxServlet extends HttpServlet {

	private static final long serialVersionUID = 6215889008198024545L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/list-all-jmx.jsp");
		
		JmxAttributeRepository repository = SingletonManager.getJmxAttributeRepository();
		request.setAttribute("jmxList", repository.getAllJmxAttributeValues());
		
		dispatcher.forward(request, response);
	}
	
	
}