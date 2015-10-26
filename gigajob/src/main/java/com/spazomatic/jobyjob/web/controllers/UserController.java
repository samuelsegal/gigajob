/**
 * 
 */
package com.spazomatic.jobyjob.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import com.spazomatic.jobyjob.profile.model.User;
import com.spazomatic.jobyjob.profile.services.UserService;
import com.spazomatic.jobyjob.web.config.SecurityUser;


@Controller
public class UserController 
{
	private static UserService userService;
	
	@Autowired
	public void setUserService(UserService userService) {
		UserController.userService = userService;
	}
	
	public static User getCurrentUser()
	{
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    if (principal instanceof UserDetails) 
	    {
	    	String login = ((UserDetails) principal).getUsername();
	    	User loginUser = userService.findUserByLogin(login);
	    	return new SecurityUser(loginUser);
	    }

	    return null;
	}
}

