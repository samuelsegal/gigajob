package com.spazomatic.jobyjob.profile.model.repos;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.spazomatic.jobyjob.profile.model.Role;

public interface RoleRepository extends JpaRepository<Role, Serializable>
{
	@Query("select r from Role r where r.role=?1")
	Role getRoleByRoleName(String roleName);
}
