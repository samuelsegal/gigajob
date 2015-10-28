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

import com.spazomatic.jobyjob.entities.IpLoc;
import com.spazomatic.jobyjob.entities.Post;
import com.spazomatic.jobyjob.profile.account.AccountRepository;
import com.spazomatic.jobyjob.profile.model.repos.UserRepository;
import com.spazomatic.jobyjob.service.PostService;

@Controller
public class HomeController {
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;	
	
	private final UserRepository userRepository;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private HttpServletRequest request;
	
	@Inject
	public HomeController(Provider<ConnectionRepository> connectionRepositoryProvider, UserRepository userRepository) {
		this.connectionRepositoryProvider = connectionRepositoryProvider;
		this.userRepository = userRepository;
	}
	@RequestMapping("/homemap")
	public String homemap(Principal currentUser, Model model) {
		//model.addAttribute("connectionsToProviders", getConnectionRepository().findAllConnections());
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.findUserByLogin(currentUser.getName()));
		log.debug(String.format("MODEL: %s", model.toString()));

		return "home";
	}		
	@RequestMapping("/profile")
	public String profile(Principal currentUser, Model model) {
		//model.addAttribute("connectionsToProviders", getConnectionRepository().findAllConnections());
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.findUserByLogin(currentUser.getName()));
		log.debug(String.format("MODEL: %s", model.toString()));
		
		return "profile/profile";
	}
	
	@RequestMapping(value = { "/table" }, method = RequestMethod.GET)
	public String table(Principal currentUser, Model model,
			@RequestParam(value = "distance", 
							required = false, 
							defaultValue = "20km") 
							String distance) {
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.findUserByLogin(currentUser.getName()));
		
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		log.debug(String.format("Client ip: %s", ipAddress));
		RestTemplate restTemplate = new RestTemplate();
		IpLoc ipLoc = restTemplate.getForObject(String.format(
				"http://www.telize.com/geoip/%s", ipAddress),
				IpLoc.class);
		log.debug(String.format("IP lat / lng: %f / %f", ipLoc.getLatitude(), ipLoc.getLongitude()));
		Page<Post> posts = postService.findBySpatialDistance(
				distance, new GeoPoint(45.7806d, 3.0875d), new PageRequest(1,10));

		model.addAttribute("posts", posts);
		return "data/table";
	}
	
	@RequestMapping("/")
	public String home(Principal currentUser, Model model) {
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.findUserByLogin(currentUser.getName()));
		log.debug(String.format("MODEL: %s", model.toString()));
		return "home";
	}
	
	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}
}
