package com.spazomatic.jobyjob.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spazomatic.jobyjob.db.model.UserProfile;
import com.spazomatic.jobyjob.nosql.entities.IpLoc;
import com.spazomatic.jobyjob.nosql.entities.Post;
import com.spazomatic.jobyjob.service.PostService;
import com.spazomatic.jobyjob.util.SocialControllerUtil;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class PostController {
	
	private static final Logger LOG = LoggerFactory.getLogger(
			String.format("%s :: %s",Util.LOG_TAG,PostController.class));
	
	@Autowired private PostService postService;	
	@Autowired private HttpServletRequest request;	
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
		HttpSession session = request.getSession();
		session.setAttribute("rib", post);
		return "data/viewPost";
	}

	@RequestMapping( value = { "/postRibSubmit" }, method = RequestMethod.POST)
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

		setPostImages(post, fileInput1, fileInput2, 
				fileInput3, fileInput4, fileInput5);

        if(loc != null && loc.getLatitude() != null && loc.getLongitude() != null){
        	post.setLocation(new double[]{loc.getLatitude(), loc.getLongitude()});
        }else if(loc != null && loc.getStreetNum() != null){
        	post.setIpLoc(loc);
        	//String formattedAdd = String.format("%s %s %s, %s %s", 
        	//		loc.getStreetNum(),loc.getStreetName(),
        	//		loc.getCity(),loc.getState(),loc.getPostal_code());
        	//post.setFormattedAddress(formattedAdd);
        }
        
        if(post.getTags() != null && post.getTags().get(0) != null){
        	String titlePart1 = post.getTags().get(0).getId() != null ? 
        			post.getTags().get(0).getId() : "NO";
        	String titlePart2 =  post.getTags().get(0).getName() != null ? 
        			post.getTags().get(0).getName() : "TITLE";
        	post.setTitle(String.format("%s %s", titlePart1, titlePart2));
        }
        if(currentUser == null){
			HttpSession session = request.getSession();
			session.setAttribute("rib", post);
			return "redirect:/sec_submitRib";
		}		
		util.setModel(request, currentUser, model);		
		post.setUserId(currentUser.getName());		
		HttpSession session = request.getSession();
		UserProfile client = (UserProfile) session.getAttribute(
				SocialControllerUtil.USER_PROFILE);
		post.setClientName(client.getName());
		session.setAttribute("rib", post);
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
		if(rib.getLocation()!= null){
			double[] latlon = rib.getLocation();
			loca.setLatitude(latlon[0]);
			loca.setLongitude(latlon[1]);
		}
		model.addAttribute("rib", rib);
		model.addAttribute("loc", loca);
		return "client/confirmSubmitRib";

	}

	@RequestMapping(value= {"/getImgFile/{id}"}, method = RequestMethod.GET)
	public void getUserImage(HttpServletResponse response, 
			Principal currentUser, Model model, 
			@PathVariable("id") String postNailId) throws IOException{
		
		HttpSession session = request.getSession();
		Post rib = session.getAttribute("rib") != null 
				? (Post) session.getAttribute("rib") 
				: new Post();
		if(rib.getImgFiles() != null && 
				rib.getImgFiles().get(postNailId) != null){
			//TODO: setContentType dynamic		
			response.setContentType("image/png");
			byte[] buffer = rib.getImgFiles().get(postNailId);
	
			InputStream in1 = new ByteArrayInputStream(buffer);
			IOUtils.copy(in1, response.getOutputStream());   
		}
	}	

	private void setPostImages(Post post, MultipartFile fileInput1, 
			MultipartFile fileInput2, MultipartFile fileInput3,
			MultipartFile fileInput4, MultipartFile fileInput5) {
		
    	Map<String, byte[]> imgFiles = new HashMap<>();
        try {
	    	if (!fileInput1.isEmpty()) {          	
	        	imgFiles.put("fileInput1", fileInput1.getBytes());    	        	
	    	}
	    	if (!fileInput2.isEmpty()) {          	
	        	imgFiles.put("fileInput2", fileInput2.getBytes());    
	    	}
	    	if (!fileInput3.isEmpty()) {          	
	        	imgFiles.put("fileInput3", fileInput3.getBytes());    
	    	}
	    	if (!fileInput4.isEmpty()) {          	
	        	imgFiles.put("fileInput4", fileInput4.getBytes());    
	    	}
	    	if (!fileInput5.isEmpty()) {          	
	        	imgFiles.put("fileInput5", fileInput5.getBytes());    	        	
	    	}
	    	
	    	post.setImgFiles(imgFiles);
	    	
        } catch (Exception e) {
        	LOG.error(String.format(
        			"ERROR setting Post Image files :: %s", 
        			e.getMessage()));
        }     			
	}
	
}
