package com.spazomatic.jobyjob.controllers;

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
		//RestTemplate restTemplate = new RestTemplate();
		//IpLoc ipLoc = restTemplate.getForObject(String.format(
		//		"http://www.telize.com/geoip/%s", ipAddress),
		//		IpLoc.class);

		post.setLocation(new double[]{loc.getLatitude(), loc.getLongitude()});
		postService.save(post);	
		
		Page<Post> posts = postService.findByLocationNear(new Point(32.0957d, -81.2531),
				"30", new PageRequest(0,10));
		ObjectMapper mapper = new ObjectMapper();
		try {
			String postsAsJSON = mapper.writeValueAsString(posts);
			model.addAttribute("postsAsJSON",postsAsJSON);
			log.debug("JJJJJSSSSSSSOOOOOOOONNNNN: " + postsAsJSON);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		model.addAttribute("posts", posts.getContent());
		return "client/userPosts";
	}
	
	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}
}
