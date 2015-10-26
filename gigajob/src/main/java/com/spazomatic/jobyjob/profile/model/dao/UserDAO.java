package com.spazomatic.jobyjob.profile.model.dao;

import java.util.List;

import com.spazomatic.jobyjob.profile.model.User;

public interface UserDAO {

	public User getUser(String login);

	List<User> findAll();

	User findUserById(int id);

	User create(User user);

	User login(String login, String password);

}
