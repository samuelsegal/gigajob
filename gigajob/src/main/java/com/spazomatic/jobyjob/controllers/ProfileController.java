package com.spazomatic.jobyjob.controllers;

import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.db.model.UserProfile;
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
	
	@RequestMapping(value = { "/updateProfile" }, method = RequestMethod.POST)
	public String updateProviderProfile(Principal currentUser, Model model,
			@ModelAttribute UserProfile gigauser, @ModelAttribute IpLoc loc) {

		LOG.debug(String.format("Updating User %s", gigauser));
		util.setModel(request, currentUser, model);
		//HttpSession session = request.getSession();
		// client = (UserProfile) session.getAttribute(SocialControllerUtil.USER_PROFILE);

		return "profile/profile";
	}
	
	@RequestMapping(value = { "/assignRole" }, method = RequestMethod.POST)
	public String assignRole(Principal currentUser, Model model,
			@RequestParam(value = "roleParam") String roleParam) {
		
		usersDao.addRole(currentUser.getName(), roleParam);
		util.updateSession(request.getSession(),currentUser.getName());
		util.setModel(request, currentUser, model);
		
		if(Util.ROLE_CLIENT.equals(roleParam)){
			return "client/createClientProfile";
		}else if(Util.ROLE_PROVIDER.equals(roleParam)){
			GigaProvider gigaProvider = getGigaProvider(currentUser);
			IpLoc ipLoc = new IpLoc();
			model.addAttribute("gigaProvider",gigaProvider);
			return "provider/providerProfile";
		}else{
			return "error";
		}
		
	}

	private GigaProvider getGigaProvider(Principal currentUser) {
		GigaProvider gigaProvider = gigaProviderService.findByUserId(currentUser.getName()); 
		if(gigaProvider == null){
			HttpSession session = request.getSession();
			UserProfile userProfile = (UserProfile) session.getAttribute(
					SocialControllerUtil.USER_PROFILE);
			gigaProvider = new GigaProvider();
			gigaProvider.setActive(false);
			gigaProvider.setDescription("");
			gigaProvider.setTitle(userProfile.getName() != null ? userProfile.getName() : "");
			gigaProvider.setProviderName(userProfile.getName() != null ? userProfile.getName() : "");
		}
		return gigaProvider;
	}
	
}
