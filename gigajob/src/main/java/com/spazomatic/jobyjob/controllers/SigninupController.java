package com.spazomatic.jobyjob.controllers;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spazomatic.jobyjob.db.dao.DataDao;
import com.spazomatic.jobyjob.nosql.entities.Post;
import com.spazomatic.jobyjob.util.SocialControllerUtil;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class SigninupController {

    private static final Logger LOG = LoggerFactory.getLogger(Util.LOG_TAG);
    @Autowired private ConnectionRepository connectionRepository;
    @Autowired private DataDao dataDao;
    @Autowired private SocialControllerUtil util;
  
    public SigninupController() {
	}
    
	@RequestMapping("/signin")
    public String signin(HttpServletRequest request, Principal currentUser, Model model,
    		Post post,RedirectAttributes redirectAttributes) {
        if(currentUser != null){
        	util.setModel(request, currentUser, model);
        }else{
	        String referrer = request.getHeader("Referer");
	        LOG.debug("REFERER:::::::::"+referrer);
	        request.getSession().setAttribute("url_prior_login", referrer);
        }
        return "login";
    }
    @RequestMapping("/signup")
    public String signup(HttpServletRequest request, Principal currentUser, Model model,
    		Post post,RedirectAttributes redirectAttributes) {
        return "user/registrationForm";
    }
    @RequestMapping(value= "/update", method = POST)
    public String update(
        HttpServletRequest request,
        Principal currentUser,
        Model model,
        @RequestParam(value = "data", required = true) String data) {

        LOG.debug("Update data to: {}", data);
        String userId = currentUser.getName();
        dataDao.setDate(userId, data);

        util.setModel(request, currentUser, model);
        return "home";
    }

    @RequestMapping(value= "/updateStatus", method = POST)
    public String updateStatus(
        WebRequest webRequest,
        HttpServletRequest request,
        Principal currentUser,
        Model model,
        @RequestParam(value = "status", required = true) String status) {
    	
    		MultiValueMap<String, Connection<?>> cmap = connectionRepository.findAllConnections();
	        LOG.error("cs.size = {}", cmap.size());
	        Set<Map.Entry<String, List<Connection<?>>>> entries = cmap.entrySet();
	        for (Map.Entry<String, List<Connection<?>>> entry : entries) {
	            for (Connection<?> c : entry.getValue()) {
	                LOG.debug("Updates {} with the status '{}'", entry.getKey(), status);
	                c.updateStatus(status);
	            }
	        }	
	        return "home";
    }
}
