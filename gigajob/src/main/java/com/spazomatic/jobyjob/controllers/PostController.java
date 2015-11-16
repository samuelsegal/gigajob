package com.spazomatic.jobyjob.controllers;

import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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
import com.mongodb.gridfs.GridFSDBFile;
import com.spazomatic.jobyjob.db.model.UserProfile;
import com.spazomatic.jobyjob.location.ServerLocation;
import com.spazomatic.jobyjob.location.ServerLocationBo;
import com.spazomatic.jobyjob.nosql.entities.IpLoc;
import com.spazomatic.jobyjob.nosql.entities.Post;
import com.spazomatic.jobyjob.service.PostService;
import com.spazomatic.jobyjob.util.SocialControllerUtil;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class PostController {
	
	private static final Logger LOG = LoggerFactory.getLogger(Util.LOG_TAG);
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private HttpServletRequest request;

	@Autowired
	private ServerLocationBo serverLocationBo;	
	
    @Autowired
    private SocialControllerUtil util;
    
	public PostController() {
	}

	@RequestMapping(value = { "/viewPost" }, method = RequestMethod.GET)
	public String viewPost(Principal currentUser, Model model,
			@ModelAttribute Post post, @ModelAttribute IpLoc loc,
			@RequestParam(value = "postid") String postid) {
		
		util.setModel(request, currentUser, model);
		post = postService.findOne(postid);
		model.addAttribute(post);
		return "data/viewPost";
	}

	@RequestMapping( value = { "/postRibSubmit" }, method = RequestMethod.POST )
	public String postRib(Principal currentUser, Model model,
			@ModelAttribute Post post, @ModelAttribute IpLoc loc){
				
		util.setModel(request, currentUser, model);
		return "usr/postRib";	
	}
	@RequestMapping(value = { "/submitRib" }, method = RequestMethod.POST)
	public String submitRib(Principal currentUser, Model model,
			@ModelAttribute Post post, @ModelAttribute IpLoc loc, 
			@ModelAttribute MultipartFile fileInput1, 
			@ModelAttribute MultipartFile fileInput2,
			@ModelAttribute MultipartFile fileInput3,
			@ModelAttribute MultipartFile fileInput4,
			@ModelAttribute MultipartFile fileInput5) {

    	LOG.debug("UHHHHHHHHHHHHHHHH....");
    	List<MultipartFile> imgFiles = new ArrayList<>();
        if (!fileInput1.isEmpty()) {
        	LOG.debug("FILE NOT EMPTY, WHA WHA WHA WHA????");
            try {            	
            	//store files with post
            	imgFiles.add(fileInput1);    
            	post.setImgFiles(imgFiles);
            } catch (Exception e) {
            	LOG.error("SHHHEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEET");
             //   return "You failed to upload " + fileInput1.getName() + " => " + e.getMessage();
            }
        }	
		
		util.setModel(request, currentUser, model);
		post.setLocation(new double[]{loc.getLatitude(), loc.getLongitude()});
		post.setUserId(currentUser.getName());
		
		HttpSession session = request.getSession();
		UserProfile client  = (UserProfile) session.getAttribute(SocialControllerUtil.USER_PROFILE);
		post.setClientName(client.getName());
		if(imgFiles.isEmpty()){
			postService.save(post);
		}else{
			postService.save(post);
		}
		
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
