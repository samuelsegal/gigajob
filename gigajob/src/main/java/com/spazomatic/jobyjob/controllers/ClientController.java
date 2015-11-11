package com.spazomatic.jobyjob.controllers;

import java.io.IOException;
import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.db.model.UserProfile;
import com.spazomatic.jobyjob.location.ServerLocation;
import com.spazomatic.jobyjob.location.ServerLocationBo;
import com.spazomatic.jobyjob.nosql.entities.IpLoc;
import com.spazomatic.jobyjob.nosql.entities.Post;
import com.spazomatic.jobyjob.service.PostService;
import com.spazomatic.jobyjob.util.SocialControllerUtil;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class ClientController {
	
	private static final Logger LOG = LoggerFactory.getLogger(Util.LOG_TAG);
	
	private final UsersDao usersDao;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private ServerLocationBo serverLocationBo;	
	
	@Autowired
	private HttpServletRequest request;

    @Autowired
    private SocialControllerUtil util;
    
	@Inject
	public ClientController(UsersDao usersDao) {
		this.usersDao = usersDao;
	}
	
	@RequestMapping(value = { "/postJob" }, method = RequestMethod.GET)
	public String postJob(Principal currentUser, Model model) {
		
		util.setModel(request, currentUser, model);
		Post post = new Post();
		IpLoc ipLoc = new IpLoc();
		model.addAttribute("post",post);
		model.addAttribute("loc", ipLoc);
		return "client/postJob";
		
	}
	@RequestMapping(value = { "/submitJob" }, method = RequestMethod.POST)
	public String submitJob(Principal currentUser, Model model,
			@ModelAttribute Post post, @ModelAttribute IpLoc loc) {
		
		util.setModel(request, currentUser, model);
		post.setLocation(new double[]{loc.getLatitude(), loc.getLongitude()});
		post.setUser_id(currentUser.getName());
		
		HttpSession session = request.getSession();
		UserProfile client = (UserProfile) session.getAttribute(SocialControllerUtil.USER_PROFILE);
		post.setClientName(client.getName());
		
		postService.save(post);	
		
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();		
		}
	
		ServerLocation location;
		try {
			location = getLocation(ipAddress);

			LOG.debug(String.format("Client IP Addy: %s", ipAddress));
			LOG.debug(String.format("Client Loaked the Cloak: %s", location.toString()));
			
			IpLoc ipLoc = new IpLoc();
			ipLoc.setLatitude(Double.valueOf(location.getLatitude()));
			ipLoc.setLongitude(Double.valueOf(location.getLongitude()));
			
			Page<Post> posts = postService.findByLocationNear(
					new Point(ipLoc.getLatitude(), ipLoc.getLongitude()),
					"30", new PageRequest(0,100));
			model.addAttribute("posts", posts.getContent());
			
			ObjectMapper mapper = new ObjectMapper();

				String postsAsJSON = mapper.writeValueAsString(posts);
				model.addAttribute("postsAsJSON",postsAsJSON);
				LOG.debug("postsAsJSON: " + postsAsJSON);

			model.addAttribute("posts", posts.getContent());		
		} catch (Exception e) {
			LOG.error(e.getMessage());
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
					LOG.error(e.getMessage());
					throw new Exception(e.getMessage());
				}
			}
		}
		return location;
	}

}
