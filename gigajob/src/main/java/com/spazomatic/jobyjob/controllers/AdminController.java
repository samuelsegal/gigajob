package com.spazomatic.jobyjob.controllers;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;

import com.spazomatic.jobyjob.profile.model.repos.UserRepository;
import com.spazomatic.jobyjob.service.PostService;
import com.spazomatic.jobyjob.utility.Util;

@Controller
public class AdminController {
	
	private static final Logger log = LoggerFactory.getLogger(Util.LOG_TAG);
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;	
	
	private final UserRepository userRepository;
		
	@Inject
	public AdminController(Provider<ConnectionRepository> connectionRepositoryProvider, 
			UserRepository userRepository) {
		this.connectionRepositoryProvider = connectionRepositoryProvider;
		this.userRepository = userRepository;
	}
	


	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}
}
