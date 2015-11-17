package com.spazomatic.jobyjob.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.spazomatic.jobyjob.conf.CustomUserDetailsService;
import com.spazomatic.jobyjob.db.model.User;

public class SignInUtils {

	/**
	 * Programmatically signs in the user with the given the user ID.
	 */
	public static void signin(User user) {
		SecurityContextHolder.getContext()
				.setAuthentication(
						new UsernamePasswordAuthenticationToken(
								user.getUsername(), 
								null, 
								CustomUserDetailsService.
									getGrantedAuthorities(user.getRoles())));
		
		
	}
	public static void signin(String userLogin) {
		SecurityContextHolder.getContext()
				.setAuthentication(
						new UsernamePasswordAuthenticationToken(
								userLogin, 
								null, 
								null));
	}
}
