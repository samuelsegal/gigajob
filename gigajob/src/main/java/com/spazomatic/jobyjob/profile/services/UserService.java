/**
 * 
 */
package com.spazomatic.jobyjob.profile.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
}

