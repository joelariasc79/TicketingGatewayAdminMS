package com.ticketing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticketing.domain.Role;
import com.ticketing.domain.User;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

	public Optional<Role> findByRoleName(String roleName);
	public Optional<Role> findByRoleName(Role.RoleName roleName);
	
	// Find user Roles
    List<Role> findByUser(User user);
	
}
