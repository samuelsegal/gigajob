package com.spazomatic.jobyjob.controllers;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spazomatic.jobyjob.util.SocialControllerUtil;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class MainController {

	private static Logger LOG = LoggerFactory.getLogger(Util.LOG_TAG);
    @Autowired private SocialControllerUtil util;   
    
    @RequestMapping("/")
    public String usr(HttpServletRequest request, Principal currentUser, Model model) {
    	if(currentUser != null){
    		util.setModel(request, currentUser, model);
    		return "home";
    	}
        return "usr/index";
    }    
   
    @RequestMapping("/home")
    public String home(HttpServletRequest request, Principal currentUser, Model model) {
        HttpSession session = request.getSession();
        if (session != null) {
            String redirectUrl = (String) session.getAttribute("url_prior_login");
            if (redirectUrl != null) {
                // we do not forget to clean this attribute from session
                session.removeAttribute("url_prior_login");
                // then we redirect
                util.setModel(request, currentUser, model);
                return "redirect:" + redirectUrl;
            } else {
               // super.onAuthenticationSuccess(request, response, authentication);
            }
        } 

        util.setModel(request, currentUser, model);
        return "home";
    }
    
    @RequestMapping("/login")
    public String login(){
    	return "login";
    }
    
    @RequestMapping("/forgotPassword")
    public String forgotPassword(){
    	return "forgotPassword";
    }
        
}
