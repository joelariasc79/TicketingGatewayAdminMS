package com.ticketing.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;


import com.ticketing.domain.Role;
import com.ticketing.domain.User;
import com.ticketing.dto.RoleDTO;
import com.ticketing.service.RoleService;
import com.ticketing.service.UserService;

@Controller
@RequestMapping("/api/admin/roles") // Consider a more appropriate base path if needed
public class RoleController {

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/roles/all")
    @ResponseBody
    public ResponseEntity<List<Role>> getAllRoles1() {
        return ResponseEntity.ok(roleService.findAll());
    }
    
    
    
    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<Role> roles = roleService.findAll();
        List<RoleDTO> roleDTOs = roles.stream()
                .map(role -> new RoleDTO(role.getRoleId(), role.getRoleName().toString())) // Convert RoleName to String
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleDTOs);
    }
    
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<RoleDTO>> getAllRoles(@PathVariable Long userId) {
    	
    	User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    	
        List<Role> roles = roleService.findByUser(user);
        List<RoleDTO> roleDTOs = roles.stream()
                .map(role -> new RoleDTO(role.getRoleId(), role.getRoleName().toString())) // Convert RoleName to String
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleDTOs);
    }
}