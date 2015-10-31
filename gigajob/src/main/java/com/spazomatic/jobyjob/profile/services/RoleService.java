package com.spazomatic.jobyjob.profile.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spazomatic.jobyjob.profile.model.Role;
import com.spazomatic.jobyjob.profile.model.repos.RoleRepository;

@Service
@Transactional
public class RoleService {
	
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_CLIENT = "ROLE_CLIENT";
	public static final String ROLE_PROVIDER = "ROLE_PROVIDER";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_GOD = "ROLE_GOD";
			
	@Autowired
	RoleRepository roleRepository;
	
	//TODO: Framework question: Should Services be static? Should RoleService be singleton?
	public Role findRoleById(int id) {
		return roleRepository.findOne(id);
	}
	
	public Role findRoleByRoleName(String roleName){
		return roleRepository.getRoleByRoleName(roleName);				
	}
}
