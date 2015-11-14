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
package com.spazomatic.jobyjob.controllers;

import java.io.IOException;
import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.spazomatic.jobyjob.db.dao.DataDao;
import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.location.ServerLocation;
import com.spazomatic.jobyjob.location.ServerLocationBo;
import com.spazomatic.jobyjob.nosql.entities.GigaProvider;
import com.spazomatic.jobyjob.nosql.entities.IpLoc;
import com.spazomatic.jobyjob.nosql.entities.Post;
import com.spazomatic.jobyjob.service.GigaProviderService;
import com.spazomatic.jobyjob.service.PostService;
import com.spazomatic.jobyjob.util.SocialControllerUtil;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class HomeController {
	
	private static final Logger LOG = LoggerFactory.getLogger(Util.LOG_TAG);	
	
	private final UsersDao usersDao;

    @Autowired
    private DataDao dataDao;

    @Autowired
    private SocialControllerUtil util;
    
	@Autowired
	private PostService postService;

	@Autowired
	private GigaProviderService gigaProviderService;
	
	@Autowired
	private ServerLocationBo serverLocationBo;
	
	@Autowired
	private HttpServletRequest request;
	
	@Inject
	public HomeController(UsersDao userRepository) {
		this.usersDao = userRepository;
	}
	
	@RequestMapping("/homemap")
	public String homemap(Principal currentUser, Model model) {
		
		util.setModel(request, currentUser, model);		
		return "home";
		
	}		
	@RequestMapping("/profile")
	public String profile(Principal currentUser, Model model) {
		
		util.setModel(request, currentUser, model);		
		return "profile/profile";
		
	}
	
	@RequestMapping(value = { "/listPosts" }, method = RequestMethod.GET)
	public String listPosts(Principal currentUser, Model model,
			@RequestParam(value = "distance", 
							required = false, 
							defaultValue = "30") 
							String distance) {
		
		util.setModel(request, currentUser, model);
		try {		
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();		
			}
		
			ServerLocation location = getLocation(ipAddress);
			LOG.debug(String.format("Client IP Addy: %s", ipAddress));
			LOG.debug(String.format("Client Loaked the Cloak: %s", location.toString()));
			
			IpLoc ipLoc = new IpLoc();
			ipLoc.setLatitude(Double.valueOf(location.getLatitude()));
			ipLoc.setLongitude(Double.valueOf(location.getLongitude()));
			Page<Post> posts = postService.findByLocationNear(
					new Point(ipLoc.getLatitude(), ipLoc.getLongitude()),
					distance, new PageRequest(0,200));
			model.addAttribute("posts", posts.getContent());
			
			ObjectMapper mapper = new ObjectMapper();
			String postsAsJSON = mapper.writeValueAsString(posts);
			model.addAttribute("postsAsJSON",postsAsJSON);
			LOG.debug("PostsAsJSON: " + postsAsJSON);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			model.addAttribute("message",e.getMessage());
			return "error";
		}
		
		return "data/listPosts";
	}
	
	@RequestMapping(value = { "/listProviders" }, method = RequestMethod.GET)
	public String listProviders(Principal currentUser, Model model,
			@RequestParam(value = "distance", 
							required = false, 
							defaultValue = "30") 
							String distance) {
		
		util.setModel(request, currentUser, model);
		try {		
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();		
			}
		
			ServerLocation location = getLocation(ipAddress);
			LOG.debug(String.format("Client IP Addy: %s", ipAddress));
			LOG.debug(String.format("Client Loaked the Cloak: %s", location.toString()));
			
			IpLoc ipLoc = new IpLoc();
			ipLoc.setLatitude(Double.valueOf(location.getLatitude()));
			ipLoc.setLongitude(Double.valueOf(location.getLongitude()));
			Page<GigaProvider> gigaProviders = gigaProviderService.findByLocationNear(
					new Point(ipLoc.getLatitude(), ipLoc.getLongitude()),
					"90000", new PageRequest(0,200));
			//Iterable<GigaProvider> gigaProviders = gigaProviderService.findAll();
			model.addAttribute("gigaProviders", gigaProviders);
			ObjectMapper mapper = new ObjectMapper();
			String postsAsJSON = mapper.writeValueAsString(gigaProviders);
			model.addAttribute("postsAsJSON",postsAsJSON);
			LOG.debug("PostsAsJSON: " + postsAsJSON);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			model.addAttribute("message",e.getMessage());
			return "error";
		}
		
		return "data/listGigaProviders";
	}

	@RequestMapping( value = { "/postRib" }, method = RequestMethod.GET )
	public String postRib(Principal currentUser, Model model){
				
		util.setModel(request, currentUser, model);
		return "usr/postRib";
	
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
					LOG.error(e.getMessage());
					throw new Exception(e.getMessage());
				}
			}
		}
		return location;
	}

}
