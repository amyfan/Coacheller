package com.ratethisfest.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ratethisfest.server.logic.LollaSetDataLoader;

public class LoadDataServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(LoadDataServlet.class.getName());
	  
	  @Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		  log.info("LoadDataServlet.service() Started with method: " + req.getMethod());
//		  	if (req.getMethod() != "POST") {
//		  		return;
//		  	}
		  	//log.info("LoadDataServlet.service() Confirmed this is a post");
		  	
		  	resp.setContentType("text/plain");
		    resp.getWriter().println("Hello");
		    
		  	BufferedReader inputReader = new BufferedReader(new InputStreamReader(req.getInputStream()));
		  	String nextLine;
		  	log.info("Remainder of request follows:");
		  	while ((nextLine = inputReader.readLine()) != null) {
		  		log.info(nextLine);
		  	}
		}
	  
	  @Override
	  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		  log.info("LoadDataServlet.service() Started with method: " + req.getMethod());
//		  	if (req.getMethod() != "POST") {
//		  		return;
//		  	}
		  	//log.info("LoadDataServlet.service() Confirmed this is a post");
		  	
		  	resp.setContentType("text/plain");
		    resp.getWriter().println("Hello");
		    
		  	BufferedReader inputReader = new BufferedReader(new InputStreamReader(req.getInputStream()));
		  	String nextLine;
		  	//log.info("Remainder of request follows:");
		  	//while ((nextLine = inputReader.readLine()) != null) {
		  		//log.info(nextLine);
		  	//}
		  	
		  	LollaSetDataLoader.getInstance().insertSetsFromFile(inputReader);
		}

}
