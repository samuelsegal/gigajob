package com.spazomatic.jobyjob.controllers;

import java.security.Principal;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spazomatic.jobyjob.entities.IpLoc;
import com.spazomatic.jobyjob.entities.Post;
import com.spazomatic.jobyjob.profile.model.repos.UserRepository;
import com.spazomatic.jobyjob.service.PostService;

@Controller
public class ProviderController {
	private static final Logger log = LoggerFactory.getLogger("spazomatic.gigajob");
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;	
	
	private final UserRepository userRepository;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private HttpServletRequest request;
	
	
	@Inject
	public ProviderController(Provider<ConnectionRepository> connectionRepositoryProvider, UserRepository userRepository) {
		this.connectionRepositoryProvider = connectionRepositoryProvider;
		this.userRepository = userRepository;
	}

	
	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}
}