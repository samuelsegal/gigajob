package com.spazomatic.jobyjob.controllers;

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
	
	@Autowired private PostService postService;	
	@Autowired private HttpServletRequest request;
	@Autowired private ServerLocationBo serverLocationBo;		
    @Autowired private SocialControllerUtil util;
    
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

    	List<MultipartFile> imgFiles = new ArrayList<>();
        if (!fileInput1.isEmpty()) {
            try {            	
            	//store files with post
            	imgFiles.add(fileInput1);    
            	post.setImgFiles(imgFiles);
            } catch (Exception e) {
            	LOG.error(String.format(
            			"ERROR setting Post Image files :: %s", 
            			e.getMessage()));
            }
        }	
        if(loc != null && loc.getLatitude() != null && loc.getLongitude() != null){
        	post.setLocation(new double[]{loc.getLatitude(), loc.getLongitude()});
        }
		if(currentUser == null){
			HttpSession session = request.getSession();
			session.setAttribute("rib", post);
			return "redirect:/sec_submitRib";
		}		
		util.setModel(request, currentUser, model);		
		post.setUserId(currentUser.getName());		
		HttpSession session = request.getSession();
		UserProfile client  = (UserProfile) session.getAttribute(
				SocialControllerUtil.USER_PROFILE);
		post.setClientName(client.getName());
		
		model.addAttribute("rib", post);
		model.addAttribute("loc", loc);
		return "client/confirmSubmitRib";
	
	}

	@RequestMapping(value = { "/sec_submitRib" }, method = RequestMethod.GET)
	public String sec_submitRib(Principal currentUser, Model model) {
		HttpSession session = request.getSession();
		
		util.setModel(request, currentUser, model);
		Post rib =  session.getAttribute("rib") != null ? 
				(Post) session.getAttribute("rib") : new Post();
		IpLoc loca = new IpLoc();
		model.addAttribute("rib", rib);
		model.addAttribute("loc", loca);
		return "client/confirmSubmitRib";

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
