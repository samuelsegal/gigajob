package com.spazomatic.jobyjob.controllers;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
}
