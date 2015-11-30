package com.spazomatic.jobyjob.controllers;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class AdminController {
	
	private static final Logger LOG = LoggerFactory.getLogger(
			String.format("%s :: %s",Util.LOG_TAG,AdminController.class));

	public AdminController() {
	}
	
    @RequestMapping("/playground")
    public String playground(){
    	LOG.debug("PLAYGROUND SANDBOX MAIN AREAS DONT LEAVE YOUR STATION");
    	return "sandbox/playground";
    }
    @RequestMapping("/expandable")
    public String expandable(){
    	LOG.debug("PLAYGROUND SANDBOX MAIN AREAS DONT LEAVE YOUR STATION");
    	return "sandbox/expandable";
    }
}
