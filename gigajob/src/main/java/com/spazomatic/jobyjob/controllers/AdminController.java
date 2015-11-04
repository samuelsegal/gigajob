package com.spazomatic.jobyjob.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spazomatic.jobyjob.entities.Post;
import com.spazomatic.jobyjob.profile.model.repos.UserRepository;
import com.spazomatic.jobyjob.service.PostService;

@Controller
public class AdminController {
	
	private static final Logger log = LoggerFactory.getLogger("spazomatic.gigajob");
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;	
	
	private final UserRepository userRepository;
	
    @Autowired
    private PostService postService;
    
    @Autowired
    private MongoTemplate mogoTemplate;
		
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
