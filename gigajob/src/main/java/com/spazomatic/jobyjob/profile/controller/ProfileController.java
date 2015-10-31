/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spazomatic.jobyjob.profile.controller;

import java.security.Principal;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

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

import com.spazomatic.jobyjob.profile.model.Role;
import com.spazomatic.jobyjob.profile.model.User;
import com.spazomatic.jobyjob.profile.model.repos.UserRepository;
import com.spazomatic.jobyjob.profile.services.RoleService;
import com.spazomatic.jobyjob.profile.services.UserService;

@Controller
public class ProfileController {
	private static final Logger log = LoggerFactory.getLogger("spazomatic.gigajob");
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;	
	
	private final UserRepository userRepository;

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Inject
	public ProfileController(Provider<ConnectionRepository> connectionRepositoryProvider, UserRepository userRepository) {
		this.connectionRepositoryProvider = connectionRepositoryProvider;
		this.userRepository = userRepository;
	}
		
	@RequestMapping("/editProfile")
	public String editProfile(Principal currentUser, Model model) {
		
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);
		model.addAttribute("gigauser",userRepository.findUserByLogin(currentUser.getName()));
		
		return "profile/editProfile";
	}
	
	@RequestMapping("/assignRole")
	public String assignRole(Principal currentUser, Model model,
			@RequestParam(value = "roleParam") String roleParam) {

		User boozer = userRepository.findUserByLogin(currentUser.getName());
		Role role = roleService.findRoleByRoleName(roleParam);
		Set<Role> roles = boozer.getRoles();
		roles.add(role);
		boozer.setRoles(roles);
		userService.update(boozer);
		
		model.addAttribute("gigauser", boozer);
		
		Connection<Facebook> connection = getConnectionRepository().findPrimaryConnection(Facebook.class);
		model.addAttribute("fb_connection", connection != null ? connection : null);		
		
		if(RoleService.ROLE_CLIENT.equals(roleParam)){
			return "client/createClientProfile";
		}else if(RoleService.ROLE_PROVIDER.equals(roleParam)){
			return "provider/createProviderProfile";
		}else{
			return "error";
		}
		
	}
	
	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}
}
