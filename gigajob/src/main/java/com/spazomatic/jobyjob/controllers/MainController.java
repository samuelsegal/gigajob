package com.spazomatic.jobyjob.controllers;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spazomatic.jobyjob.util.SocialControllerUtil;

@Controller
public class MainController {

    @Autowired private SocialControllerUtil util;
    
    @RequestMapping("/")
    public String usr() {
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

}
