package com.spazomatic.jobyjob.profile.model.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.spazomatic.jobyjob.profile.model.Role;

@Repository
@Transactional
public class JpaRoleDAOImpl implements RoleDAO {
     
	@PersistenceContext
	private EntityManager em;
	
    public Role getRole(int id) {
    	return em.find(Role.class, id);
    }
 
}