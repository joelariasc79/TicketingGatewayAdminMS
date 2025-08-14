package com.ticketing.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.ticketing.domain.Role;
import com.ticketing.domain.User;
import com.ticketing.dto.UserDto;
import com.ticketing.service.UserService;

import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/admin/managers") // Consider a more appropriate base path if needed
public class ManagerController {
	
	@Autowired
    private UserService userService;
	
	@GetMapping("/all")
	public ResponseEntity<List<UserDto>> getAllManagers() {
	    List<User> managers = userService.findAll();
	    List<UserDto> managerDTOs = managers.stream()
	            .filter(user -> user.getRoles().stream()
	                    .anyMatch(role -> Role.RoleName.MANAGER.equals(role.getRoleName())))
	            .map(user -> new UserDto(user.getUserId(), user.getUserName()))
	            .collect(Collectors.toList());
	    return ResponseEntity.ok(managerDTOs);
	}

}
