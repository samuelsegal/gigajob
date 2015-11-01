/**
 * 
 */
package com.spazomatic.jobyjob.profile.services;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spazomatic.jobyjob.profile.model.User;
import com.spazomatic.jobyjob.profile.model.repos.UserRepository;

@Service
@Transactional
public class UserService 
{
	//private UserDao userDao;
	
	@Autowired
	private UserRepository userRepository;
	
	/*
	@Autowired
	public UserService(UserDao userDao) {
		this.userDao = userDao;
	}
	*/
	public List<User> findAll() {
		//return userDao.findAll();
		return userRepository.findAll();
	}

	public User create(User user) {
		//return userDao.create(user);
		return userRepository.save(user);
	}

	public User findUserById(int id) {
		//return userDao.findUserById(id);
		return userRepository.findOne(id);
	}

	public User login(String login, String password) {
		//return userDao.login(email,password);
		//return userRepository.login(email,password);
		return userRepository.findByLoginAndPassword(login,password);
	}

	public User update(User user) {
		return userRepository.save(user);
	}

	public void deleteUser(int id) {
		userRepository.delete(id);
	}
	
	public User findUserByLogin(String login) {
		return userRepository.findUserByLogin(login);
	}	
	
	public void updateSessionAuthorities(User user, HttpServletRequest request, String ... roles){		
		userRepository.save(user);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
		authorities.add(new SimpleGrantedAuthority(roles[0]));
		Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(),auth.getCredentials(),authorities);
		SecurityContextHolder.getContext().setAuthentication(newAuth);
		request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
	}
}

