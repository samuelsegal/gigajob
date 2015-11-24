package com.spazomatic.jobyjob.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.db.model.UserConnection;
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
	
	private static final Logger LOG = LoggerFactory.getLogger(
			String.format("%s :: %s", Util.LOG_TAG, ClientController.class));
	
	private final UsersDao usersDao;
	
	@Autowired private PostService postService;	
	@Autowired private ServerLocationBo serverLocationBo;		
	@Autowired private HttpServletRequest request;
    @Autowired private SocialControllerUtil util;
    
	@Inject
	public ClientController(UsersDao usersDao) {
		this.usersDao = usersDao;
	}

	@RequestMapping(value= { "/viewClientProfile" }, method = RequestMethod.GET)
	public String viewClientProfile(Principal currentUser, Model model,
									@ModelAttribute UserProfile clientProfile,
									@ModelAttribute UserConnection clientSocialConnect,
									@RequestParam(value = "userid", 
									required = true) String userid){
		
		util.setModel(request, currentUser, model);
		clientProfile       = usersDao.getUserProfile(userid);
		//clientSocialConnect = usersDao.getUserConnections(userid).; 
		model.addAttribute("clientProfile",       clientProfile);
		//model.addAttribute("clientSocialConnect", clientSocialConnect);
		return "client/viewClientProfile";
		
	}
	
	@RequestMapping(value = { "/postJob" }, method = RequestMethod.GET)
	public String postJob(Principal currentUser, Model model) {
		
		util.setModel(request, currentUser, model);
		Post post   = new Post();
		IpLoc ipLoc = new IpLoc();
		model.addAttribute("post",post);
		model.addAttribute("loc", ipLoc);
		return "client/postJob";
		
	}
	

	@RequestMapping(value = { "/managePosts" }, method = RequestMethod.GET)
	public String managePosts(Principal currentUser, Model model,
			@RequestParam(value = "distance", 
							required = false, 
							defaultValue = "30") 
							String distance) {
		
		util.setModel(request, currentUser, model);
		try {		
		
			Page<Post> posts = postService.findByUserId(
					currentUser.getName(), new PageRequest(0,100));
				
			ObjectMapper mapper = new ObjectMapper();
			String postsAsJSON = mapper.writeValueAsString(posts);
			model.addAttribute("posts", posts.getContent());
			model.addAttribute("postsAsJSON",postsAsJSON);
			LOG.debug("PostsAsJSON: " + postsAsJSON);
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			model.addAttribute("message",e.getMessage());
			return "error";
		}
		
		return "client/userPosts";
	}
	
	@RequestMapping(value = { "/editPost" }, method = RequestMethod.GET)
	public String editPost(Principal currentUser, Model model,
			@RequestParam(value = "postid") 
							String postid) {
		
		util.setModel(request, currentUser, model);

		Post post = postService.findOne(postid);
		model.addAttribute("post", post);	

		IpLoc ipLoc = new IpLoc();
		ipLoc.setLatitude(Double.valueOf(post.getLocation()[0]));
		ipLoc.setLongitude(Double.valueOf(post.getLocation()[1]));
		model.addAttribute("loc",ipLoc);
		
		return "client/editPost";
	}

	@RequestMapping(value = { "/confirmSubmitRib" }, method = RequestMethod.POST)
	public String confirmSubmitRib(Principal currentUser, Model model,
			@ModelAttribute Post post, @ModelAttribute IpLoc loc) {
		

		util.setModel(request, currentUser, model);		
		//TODO: Revisit, currently getting orig post stored in session as it contains images
		HttpSession session = request.getSession();	
		post = (Post) session.getAttribute("rib");
		
		UserProfile client  = (UserProfile) session.getAttribute(
				SocialControllerUtil.USER_PROFILE);
		if(loc != null && loc.getLatitude() != null && loc.getLongitude() != null){
			post.setLocation(new double[]{loc.getLatitude(), loc.getLongitude()});	
		}		
		post.setUserId(currentUser.getName());	
		post.setClientName(client.getName());	
		postService.save(post);
		
		try{
			Page<Post> posts = postService.findByUserId(
					currentUser.getName(), new PageRequest(0,100));			
			ObjectMapper mapper = new ObjectMapper();
			String postsAsJSON = mapper.writeValueAsString(posts);
			model.addAttribute("postsAsJSON",postsAsJSON);			
			model.addAttribute("posts", posts.getContent());
			LOG.debug("postsAsJSON: " + postsAsJSON);
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
