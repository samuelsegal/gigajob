package com.spazomatic.jobyjob.controllers;

import java.security.Principal;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.nosql.entities.GigaProvider;
import com.spazomatic.jobyjob.nosql.entities.IpLoc;
import com.spazomatic.jobyjob.service.GigaProviderService;
import com.spazomatic.jobyjob.util.SocialControllerUtil;
import com.spazomatic.jobyjob.util.Util;


@Controller
public class ProfileController {
	private static final Logger LOG = LoggerFactory.getLogger(Util.LOG_TAG);
	
	private final UsersDao usersDao;

	@Autowired
	private GigaProviderService gigaProviderService;
	
    @Autowired
    private SocialControllerUtil util;
    
	@Autowired
	private HttpServletRequest request;

	@Inject
	public ProfileController(UsersDao usersDao) {

		this.usersDao = usersDao;
	}
		
	@RequestMapping("/editProfile")
	public String editProfile(Principal currentUser, Model model) {
		
		util.setModel(request, currentUser, model);
		return "profile/editProfile";
		
	}
	
	@RequestMapping("/assignRole")
	public String assignRole(Principal currentUser, Model model,
			@RequestParam(value = "roleParam") String roleParam) {
		
		usersDao.addRole(currentUser.getName(), roleParam);
		util.updateSession(request.getSession(),currentUser.getName());
		util.setModel(request, currentUser, model);
		
		if(Util.ROLE_CLIENT.equals(roleParam)){
			return "client/createClientProfile";
		}else if(Util.ROLE_PROVIDER.equals(roleParam)){
			GigaProvider gigaProvider = gigaProviderService.findByUserId(currentUser.getName()) != null 
					? gigaProviderService.findByUserId(currentUser.getName()) 
					: new GigaProvider();
			IpLoc ipLoc = new IpLoc();
			model.addAttribute("gigaProvider",gigaProvider);
			return "provider/providerProfile";
		}else{
			return "error";
		}
		
	}
	
}
