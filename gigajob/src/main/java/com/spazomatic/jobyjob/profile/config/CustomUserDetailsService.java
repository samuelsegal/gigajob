/**
 * 
 */
package com.spazomatic.jobyjob.profile.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.spazomatic.jobyjob.profile.model.Role;
import com.spazomatic.jobyjob.profile.model.User;
import com.spazomatic.jobyjob.profile.services.UserService;
import com.spazomatic.jobyjob.web.config.SecurityUser;

@Component
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		User user = userService.findUserByLogin(userName);
		if(user == null){
			throw new UsernameNotFoundException("UserName "+userName+" not found");
		}
		return new SecurityUser(user);
	}
	public Collection getAuthorities(Integer role) {
		List authList = getGrantedAuthorities(getRoles(role));
		return authList;
	}

	public List getRoles(Integer role) {

		List roles = new ArrayList();

		if (role.intValue() == 1) {
			roles.add("ROLE_USER");
			//roles.add("ROLE_ADMIN");
		} else if (role.intValue() == 2) {
			//roles.add("ROLE_MODERATOR");
		}
		return roles;
	}

	public static List getGrantedAuthorities(List<Role> roles) {
		if(roles != null){
			List authorities = new ArrayList();
		
			for (Role role : roles) {
				authorities.add(new SimpleGrantedAuthority(role.getRole()));
			}
		
			return authorities;
		}
		return null;
	}

}
