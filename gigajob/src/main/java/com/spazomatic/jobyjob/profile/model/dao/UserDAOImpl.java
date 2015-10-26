package com.spazomatic.jobyjob.profile.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spazomatic.jobyjob.profile.model.User;

@Repository
public class UserDAOImpl implements UserDAO {
     
    @Autowired
    private SessionFactory sessionFactory;
     
    private Session openSession() {
        return sessionFactory.getCurrentSession();
    }
 
    public User getUser(String login) {
        List<User> userList = new ArrayList<User>();
        Query query = openSession().createQuery("from User u where u.login = :login");
        query.setParameter("login", login);
        userList = query.list();
        if (userList.size() > 0)
            return userList.get(0);
        else
            return null;    
    }

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findUserById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User create(User user) {
		openSession().save(user);
		return null;
	}

	@Override
	public User login(String login, String password) {
		// TODO Auto-generated method stub
		return null;
	}
}