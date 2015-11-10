package com.spazomatic.jobyjob.conf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.spazomatic.jobyjob.model.Authority;
import com.spazomatic.jobyjob.model.User;

public class SecurityUser extends User implements UserDetails
{

	private static final long serialVersionUID = 1L;
	public SecurityUser(User user) {
		if(user != null)
		{
			this.setRoles(user.getRoles());
			this.setEnabled(user.getEnabled());
			this.setUsername(user.getUsername());
	
			//this.setId(user.getId());
			//this.setLogin(user.getLogin());
			//this.setEmail(user.getEmail());
			this.setPassword(user.getPassword());
			//this.setDob(user.getDob());
			//this.setFirstName(user.getFirstName());
			//this.setLastName(user.getLastName());
			//this.setRoles(user.getRoles());
		}		
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		Collection<GrantedAuthority> roles = new ArrayList<>();
		List<Authority> userRoles = this.getRoles();
		
		if(userRoles != null)
		{
			for (Authority role : userRoles) {
				SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getAuthority());
				roles.add(authority);
			}
		}
		return roles;
	}

	@Override
	public String getPassword() {
		return super.getPassword();
	}

	@Override
	public String getUsername() {
		return super.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
