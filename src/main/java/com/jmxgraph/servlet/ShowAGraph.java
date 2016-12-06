package com.jmxgraph.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "GraphServlet", urlPatterns = { "/simple-graph" })
public class ShowAGraph extends HttpServlet {

	private static final long serialVersionUID = -1256646607969719740L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/jsp/simple-graph.jsp").forward(request, response);
	}
}
