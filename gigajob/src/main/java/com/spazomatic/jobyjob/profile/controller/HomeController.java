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

import java.io.IOException;
import java.security.Principal;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Point;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.spazomatic.jobyjob.entities.IpLoc;
import com.spazomatic.jobyjob.entities.Post;
import com.spazomatic.jobyjob.location.ServerLocation;
import com.spazomatic.jobyjob.location.ServerLocationBo;
import com.spazomatic.jobyjob.profile.model.User;
import com.spazomatic.jobyjob.profile.model.repos.UserRepository;
import com.spazomatic.jobyjob.service.PostService;

@Controller
public class HomeController {
	private static final Logger log = LoggerFactory.getLogger("spazomatic.gigajob");
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;	
	
	private final UserRepository userRepository;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private ServerLocationBo serverLocationBo;
	
	@Autowired
	private HttpServletRequest request;
	
	@Inject
	public HomeController(Provider<ConnectionRepository> connectionRepositoryProvider, UserRepository userRepository) {
		this.connectionRepositoryProvider = connectionRepositoryProvider;
		this.userRepository = userRepository;
	}
	@RequestMapping("/homemap")
	public String homemap(Principal currentUser, Model model) {
		
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.findUserByLogin(currentUser.getName()));
		log.debug(String.format("MODEL: %s", model.toString()));

		return "home";
	}		
	@RequestMapping("/profile")
	public String profile(Principal currentUser, Model model) {
		
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.findUserByLogin(currentUser.getName()));
		User loser = userRepository.findUserByLogin(currentUser.getName());
		log.debug("gigauser" + loser.getRoles());
		return "profile/profile";
	}
	
	@RequestMapping(value = { "/table" }, method = RequestMethod.GET)
	public String table(Principal currentUser, Model model,
			@RequestParam(value = "distance", 
							required = false, 
							defaultValue = "30") 
							String distance) {
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.findUserByLogin(currentUser.getName()));
		try {		
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();		
			}
		
			ServerLocation location = getLocation(ipAddress);
			log.debug(String.format("Client IP Addy: %s", ipAddress));
			log.debug(String.format("Client Loaked the Cloak: %s", location.toString()));
			
			IpLoc ipLoc = new IpLoc();
			ipLoc.setLatitude(Double.valueOf(location.getLatitude()));
			ipLoc.setLongitude(Double.valueOf(location.getLongitude()));
			Page<Post> posts = postService.findByLocationNear(
					new Point(ipLoc.getLatitude(), ipLoc.getLongitude()),
					distance, new PageRequest(0,20));
			model.addAttribute("posts", posts.getContent());
			
			ObjectMapper mapper = new ObjectMapper();
			String postsAsJSON = mapper.writeValueAsString(posts);
			model.addAttribute("postsAsJSON",postsAsJSON);
			log.debug("PostsAsJSON: " + postsAsJSON);
		} catch (Exception e) {
			log.error(e.getMessage());
			model.addAttribute("message",e.getMessage());
			return "error";
		}
		
		return "data/table";
	}
	
	private ServerLocation getLocation(String ipAddress) throws Exception{
		ServerLocation location = null;
		try {
			location = serverLocationBo.getLocation(ipAddress);
		} catch (IOException | GeoIp2Exception e1) {
			if(e1 instanceof GeoIp2Exception){
				RestTemplate restTemplate = new RestTemplate();
				ipAddress = restTemplate.getForObject(
						"http://checkip.amazonaws.com",
						String.class);
				try {
					location = serverLocationBo.getLocation(ipAddress);
				} catch (IOException | GeoIp2Exception e) {
					log.error(e.getMessage());
					throw new Exception(e.getMessage());
				}
			}
		}
		return location;
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
