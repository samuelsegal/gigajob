package com.spazomatic.jobyjob.profile.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.spazomatic.jobyjob.profile.model.Role;
import com.spazomatic.jobyjob.profile.model.User;

@Repository
public class RoleDAOImpl implements RoleDAO {
     
    @Autowired
    private SessionFactory sessionFactory;
     
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
 
    public Role getRole(int id) {
        Role role = (Role) getCurrentSession().load(Role.class, id);
        return role;
    }
 
}