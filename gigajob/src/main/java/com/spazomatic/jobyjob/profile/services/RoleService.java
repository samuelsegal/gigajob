package com.spazomatic.jobyjob.profile.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spazomatic.jobyjob.profile.model.Role;
import com.spazomatic.jobyjob.profile.model.repos.RoleRepository;

@Service
@Transactional
public class RoleService {

	@Autowired
	RoleRepository roleRepository;
	
	public Role findRoleById(int id) {
		//return userDao.findUserById(id);
		return roleRepository.findOne(id);
	}

}
