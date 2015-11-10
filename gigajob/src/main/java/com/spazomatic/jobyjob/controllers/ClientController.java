package com.spazomatic.jobyjob.controllers;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.location.ServerLocation;
import com.spazomatic.jobyjob.location.ServerLocationBo;
import com.spazomatic.jobyjob.nosql.entities.IpLoc;
import com.spazomatic.jobyjob.nosql.entities.Post;
import com.spazomatic.jobyjob.service.PostService;

@Controller
public class ClientController {
	
	private static final Logger log = LoggerFactory.getLogger("spazomatic.gigajob");
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;	
	
	private final UsersDao userRepository;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private ServerLocationBo serverLocationBo;	
	
	@Autowired
	private HttpServletRequest request;
		
	@Inject
	public ClientController(Provider<ConnectionRepository> connectionRepositoryProvider, 
			UsersDao userRepository) {
		this.connectionRepositoryProvider = connectionRepositoryProvider;
		this.userRepository = userRepository;
	}
	@RequestMapping(value = { "/postJob" }, method = RequestMethod.GET)
	public String postJob(Principal currentUser, Model model) {
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.getUserProfile(currentUser.getName()));
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
		model.addAttribute("gigauser",userRepository.getUserProfile(currentUser.getName()));

		post.setLocation(new double[]{loc.getLatitude(), loc.getLongitude()});
		postService.save(post);	
		
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();		
		}
	
		ServerLocation location;
		try {
			location = getLocation(ipAddress);

			log.debug(String.format("Client IP Addy: %s", ipAddress));
			log.debug(String.format("Client Loaked the Cloak: %s", location.toString()));
			
			IpLoc ipLoc = new IpLoc();
			ipLoc.setLatitude(Double.valueOf(location.getLatitude()));
			ipLoc.setLongitude(Double.valueOf(location.getLongitude()));
			
			Page<Post> posts = postService.findByLocationNear(
					new Point(ipLoc.getLatitude(), ipLoc.getLongitude()),
					"30", new PageRequest(0,20));
			model.addAttribute("posts", posts.getContent());
			
			ObjectMapper mapper = new ObjectMapper();

				String postsAsJSON = mapper.writeValueAsString(posts);
				model.addAttribute("postsAsJSON",postsAsJSON);
				log.debug("postsAsJSON: " + postsAsJSON);

			model.addAttribute("posts", posts.getContent());		
		} catch (Exception e) {
			log.error(e.getMessage());
			model.addAttribute("message",e.getMessage());
			return "error";
		}

		return "client/userPosts";
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
	
	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}
}
