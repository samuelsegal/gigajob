package com.spazomatic.jobyjob.controllers;

import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.spazomatic.jobyjob.db.dao.AuthoritiesDao;
import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.db.model.User;
import com.spazomatic.jobyjob.db.model.UserProfile;
import com.spazomatic.jobyjob.forms.SignupForm;
import com.spazomatic.jobyjob.util.SignInUtils;
import com.spazomatic.jobyjob.util.SocialControllerUtil;

@Controller
public class SignupController {
	private static final Logger log = LoggerFactory.getLogger(SignupController.class);
	private final UsersDao usersDao;
	private final ProviderSignInUtils providerSignInUtils;
	@Autowired private SocialControllerUtil util;
	@Autowired private AuthoritiesDao roleService;
	@Autowired private HttpServletRequest httpRequest;
	
	@Inject
	public SignupController(UsersDao usersDao, 
		                    ConnectionFactoryLocator connectionFactoryLocator,
		                    UsersConnectionRepository connectionRepository) {
		this.usersDao = usersDao;
		this.providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);
	}

	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public SignupForm signupForm(WebRequest request) {
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
		if (connection != null) {
	//		request.setAttribute("message", 
	//				new Message(MessageType.INFO, "Your " + 
	//						StringUtils.capitalize(connection.getKey().getProviderId()) + 
	//						" account is not associated with a JobyJob account. If you're new, please sign up."), 
	//				WebRequest.SCOPE_REQUEST);
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
			
			providerSignInUtils.doPostSignUp(user.getUsername(), request);
			UserProfile profile = new UserProfile();
			profile.setUserId(user.getUsername());
			profile.setEmail(form.getEmail());
			profile.setFirstName(form.getFirstName());
			profile.setLastName(form.getLastName());
			profile.setName(form.getUsername());
			profile.setUsername(form.getUsername());
			usersDao.createUser(user.getUsername(), profile, user);
			//Principal principal = (Principal) SecurityContextHolder
			//		.getContext().getAuthentication().getPrincipal();
			//ModelAndView mav = new ModelAndView();
			//util.setModel(httpRequest, principal, );
			return "redirect:/home";
		}
		return null;
	}

	private User createUser(SignupForm form, BindingResult formBinding) {
		//try {
			User user = new User();
			user.setUsername(form.getUsername());
			user.setPassword(form.getPassword());
			
			return user;
		//} catch (UsernameAlreadyInUseException e) {
			//formBinding.rejectValue("username", "user.duplicateUsername", "already in use");
			//return null;
		//}
	}

}
