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
package com.spazomatic.jobyjob.profile.signup;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.Valid;

import org.elasticsearch.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import com.spazomatic.jobyjob.profile.message.Message;
import com.spazomatic.jobyjob.profile.message.MessageType;
import com.spazomatic.jobyjob.profile.model.Role;
import com.spazomatic.jobyjob.profile.model.User;
import com.spazomatic.jobyjob.profile.model.repos.UserRepository;
import com.spazomatic.jobyjob.profile.services.RoleService;
import com.spazomatic.jobyjob.profile.services.UserService;
import com.spazomatic.jobyjob.profile.signin.SignInUtils;

@Controller
public class SignupController {
	private static final Logger log = LoggerFactory.getLogger(SignupController.class);
	private final UserRepository userRepository;
	private final ProviderSignInUtils providerSignInUtils;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Inject
	public SignupController(UserRepository userRepository, 
		                    ConnectionFactoryLocator connectionFactoryLocator,
		                    UsersConnectionRepository connectionRepository) {
		this.userRepository = userRepository;
		this.providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);
	}

	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public SignupForm signupForm(WebRequest request) {
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
		if (connection != null) {
			request.setAttribute("message", 
					new Message(MessageType.INFO, "Your " + 
							StringUtils.capitalize(connection.getKey().getProviderId()) + 
							" account is not associated with a JobyJob account. If you're new, please sign up."), 
					WebRequest.SCOPE_REQUEST);
			log.debug(String.format("FACEBOOK CONNECTION: %s", connection.getDisplayName()));
			return new SignupForm();
			//TODO: fetch_userPropfile not permitted - get facebook permission right. May only need to remove address
			//return SignupForm.fromProviderUser(connection.fetchUserProfile());
		} else {
			return new SignupForm();
		}
	}

	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String signup(@Valid SignupForm form, BindingResult formBinding, WebRequest request) {
		if (formBinding.hasErrors()) {
			return null;
		}
		User user = createUser(form, formBinding);
		if (user != null) {
			SignInUtils.signin(user);
			providerSignInUtils.doPostSignUp(user.getLogin(), request);
			return "redirect:/";
		}
		return null;
	}

	private User createUser(SignupForm form, BindingResult formBinding) {
		//try {
			User user = new User();
			user.setLogin(form.getUsername());
			user.setPassword(form.getPassword());
			user.setEmail(form.getEmail());

			Role userRole = roleService.findRoleById(1);
			Set<Role> roles = Sets.newHashSet(userRole);
			user.setRoles(roles);
			userService.create(user);
			return user;
		//} catch (UsernameAlreadyInUseException e) {
			//formBinding.rejectValue("username", "user.duplicateUsername", "already in use");
			//return null;
		//}
	}

}
