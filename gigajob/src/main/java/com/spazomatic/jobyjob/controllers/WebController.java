package com.spazomatic.jobyjob.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.spazomatic.jobyjob.entities.IpLoc;
import com.spazomatic.jobyjob.entities.Post;
import com.spazomatic.jobyjob.service.PostService;

@Controller
public class WebController {

	private static final Logger log = LoggerFactory.getLogger(WebController.class);

	@Autowired
	private PostService postService;
	@Autowired
	private HttpServletRequest request;

	/* Home page. */
	/*
	@RequestMapping("/")
	public String root() {
		return "redirect:index";
	}
	
	@RequestMapping("/home")
	public String home() {
		return "home";
	}
	@RequestMapping("/login")
	public String login() {
		return "login";
	}
	@RequestMapping("/hello")
	public String hello() {
		return "hello";
	}
*/
	@RequestMapping("/index")
	public String index() {
		return "index";
	}

	@RequestMapping(value = { "/table" }, method = RequestMethod.GET)
	public String table(
			@RequestParam(value = "distance", 
							required = false, 
							defaultValue = "20km") 
							String distance, Model model) {
		
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
}
