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

import com.spazomatic.jobyjob.dao.UsersDao;


@Controller
public class ProfileController {
	private static final Logger log = LoggerFactory.getLogger("spazomatic.gigajob");
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;	
	
	private final UsersDao userRepository;

	@Autowired
	private HttpServletRequest request;
	

	
	@Inject
	public ProfileController(Provider<ConnectionRepository> connectionRepositoryProvider, UsersDao userRepository) {
		this.connectionRepositoryProvider = connectionRepositoryProvider;
		this.userRepository = userRepository;
	}
		
	@RequestMapping("/editProfile")
	public String editProfile(Principal currentUser, Model model) {
		
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.getUserProfile(currentUser.getName()));
		
		return "profile/editProfile";
	}
	
	@RequestMapping("/assignRole")
	public String assignRole(Principal currentUser, Model model,
			@RequestParam(value = "roleParam") String roleParam) {

		//User boozer = userRepository.findUserByLogin(currentUser.getName());
		//Role role = roleService.findRoleByRoleName(roleParam);
		//Set<Role> roles = boozer.getRoles();
		//roles.add(role);
		//boozer.setRoles(roles);
		//userService.update(boozer);
	
		//userService.updateSessionAuthorities(boozer, request, roleParam);

		//model.addAttribute("gigauser", boozer);

		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);		
		
		//if(RoleService.ROLE_CLIENT.equals(roleParam)){
		//	return "client/createClientProfile";
		//}else if(RoleService.ROLE_PROVIDER.equals(roleParam)){
		//	return "provider/createProviderProfile";
		//}else{
		//	return "error";
		//}
		return "error";
	}
	
	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}
}
