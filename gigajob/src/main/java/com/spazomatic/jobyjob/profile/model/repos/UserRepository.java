package com.spazomatic.jobyjob.profile.model.repos;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.spazomatic.jobyjob.profile.model.User;

public interface UserRepository extends JpaRepository<User, Serializable>{

	@Query("select u from User u where u.login=?1 and u.password=?2")
	User login(String login, String password);

	User findByLoginAndPassword(String login, String password);

	User findUserByLogin(String login);


}
