package com.spazomatic.jobyjob.controllers;

import java.security.Principal;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.spazomatic.jobyjob.entities.IpLoc;
import com.spazomatic.jobyjob.entities.Post;
import com.spazomatic.jobyjob.profile.model.repos.UserRepository;
import com.spazomatic.jobyjob.service.PostService;

@Controller
public class ClientController {
	
	private static final Logger log = LoggerFactory.getLogger("spazomatic.gigajob");
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;	
	
	private final UserRepository userRepository;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private HttpServletRequest request;
		
	@Inject
	public ClientController(Provider<ConnectionRepository> connectionRepositoryProvider, 
			UserRepository userRepository) {
		this.connectionRepositoryProvider = connectionRepositoryProvider;
		this.userRepository = userRepository;
	}
	@RequestMapping(value = { "/postJob" }, method = RequestMethod.GET)
	public String postJob(Principal currentUser, Model model) {
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.findUserByLogin(currentUser.getName()));
		Post post = new Post();
		IpLoc ipLoc = new IpLoc();
		model.addAttribute("post",post);
		model.addAttribute("loc", ipLoc);
		return "client/postJob";
	}
	@RequestMapping(value = { "/submitJob" }, method = RequestMethod.POST)
	public String submitJob(Principal currentUser, Model model,
			@ModelAttribute Post post, @ModelAttribute IpLoc loc) {
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
		log.debug(String.format("%s posting %s with at lat / lon: %f / %f", currentUser.getName(), 
				post.getTitle(), loc.getLatitude(), loc.getLongitude()));
		post.setLocation(new double[]{loc.getLatitude(), loc.getLongitude()});
		postService.save(post);	
		return "client/userPosts";
	}
	
	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}
}
