package com.spazomatic.jobyjob.controllers;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spazomatic.jobyjob.forms.SearchForm;
import com.spazomatic.jobyjob.nosql.entities.Post;
import com.spazomatic.jobyjob.service.PostService;
import com.spazomatic.jobyjob.util.SocialControllerUtil;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class SearchPostsController {
	
	private static final Logger LOG = LoggerFactory.getLogger(
			String.format("%s.%s", Util.LOG_TAG, 
					SearchPostsController.class.getSimpleName()));
	
	@Autowired private PostService postService;	
	@Autowired private HttpServletRequest request;	
    @Autowired private SocialControllerUtil util;
    
	public SearchPostsController() {
	}
	@RequestMapping(value = { "/loadSearchRibs" }, method = RequestMethod.GET)
	public String loadSearchRibs(Principal currentUser, Model model,
			@ModelAttribute SearchForm searchForm) {
		
		util.setModel(request, currentUser, model);
		return "usr/searchRibs";
	}
	
	@RequestMapping(value = { "/searchRibs" }, method = RequestMethod.GET)
	public String searchRibs(Principal currentUser, Model model,
			@ModelAttribute SearchForm searchForm) {
		
		util.setModel(request, currentUser, model);
		Page<Post> posts = postService.findByTitleLike(
				searchForm.getTitle(), new PageRequest(0,200));
		model.addAttribute("posts", posts);
		return "data/listPosts";
	}
	
}
