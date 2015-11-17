package com.spazomatic.jobyjob.conf;



import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.db.model.Authority;
import com.spazomatic.jobyjob.db.model.User;


@Component
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	UsersDao userService;
	
	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		User user = userService.findUserByName(userName);
		if(user == null){
			throw new UsernameNotFoundException("UserName "+userName+" not found");
		}
		return new SecurityUser(user);
	}
	public Collection getAuthorities(Integer role) {
		Set<Authority> authList = getGrantedAuthorities(getRoles(role));
		return authList;
	}

	public Set getRoles(Integer role) {

		Set<Authority> roles = new HashSet();//Sets.newHashSet(roleService.findRoleById(role));
		//roles.add(roleService.findRoleById(role));
/*
		if (role.intValue() == 1) {
			roles.add("ROLE_USER");
			//roles.add("ROLE_ADMIN");
		} else if (role.intValue() == 2) {
			//roles.add("ROLE_MODERATOR");
		}
		*/
		return roles;
	}

	public static Set getGrantedAuthorities(Set<Authority> roles) {
		if(roles != null){
			Set authorities = new HashSet();
		
			for (Authority role : roles) {
				authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
			}
		
			return authorities;
		}
		return null;
	}
	public static Collection<? extends GrantedAuthority> getGrantedAuthorities(List<Authority> roles) {
		if(roles != null){
			Set authorities = new HashSet();
		
			for (Authority role : roles) {
				authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
			}
		
			return authorities;
		}
		return null;
	}

}
