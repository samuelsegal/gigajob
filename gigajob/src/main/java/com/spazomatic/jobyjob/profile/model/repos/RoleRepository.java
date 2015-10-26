package com.spazomatic.jobyjob.profile.model.repos;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spazomatic.jobyjob.profile.model.Role;

public interface RoleRepository extends JpaRepository<Role, Serializable>
{

}
