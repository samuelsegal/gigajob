/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spazomatic.jobyjob.profile.controller;

import java.security.Principal;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spazomatic.jobyjob.entities.IpLoc;
import com.spazomatic.jobyjob.entities.Post;
import com.spazomatic.jobyjob.profile.model.repos.UserRepository;
import com.spazomatic.jobyjob.service.PostService;

@Controller
public class ProfileController {
	private static final Logger log = LoggerFactory.getLogger("spazomatic.gigajob");
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;	
	
	private final UserRepository userRepository;
	
	@Autowired
	private HttpServletRequest request;
	
	@Inject
	public ProfileController(Provider<ConnectionRepository> connectionRepositoryProvider, UserRepository userRepository) {
		this.connectionRepositoryProvider = connectionRepositoryProvider;
		this.userRepository = userRepository;
	}
		
	@RequestMapping("/editProfile")
	public String profile(Principal currentUser, Model model) {
		//model.addAttribute("connectionsToProviders", getConnectionRepository().findAllConnections());
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.findUserByLogin(currentUser.getName()));
		log.debug(String.format("MODEL: %s", model.toString()));
		
		return "profile/editProfile";
	}
	
	
	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}
}