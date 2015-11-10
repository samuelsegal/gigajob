package com.spazomatic.jobyjob.controllers;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;

import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class AdminController {
	
	private static final Logger log = LoggerFactory.getLogger(Util.LOG_TAG);
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;	
	
	private final UsersDao userRepository;
		
	@Inject
	public AdminController(Provider<ConnectionRepository> connectionRepositoryProvider, 
			UsersDao userRepository) {
		this.connectionRepositoryProvider = connectionRepositoryProvider;
		this.userRepository = userRepository;
	}
	


	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}
}
