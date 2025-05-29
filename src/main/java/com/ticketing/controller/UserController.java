package com.ticketing.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.ticketing.domain.Role;
import com.ticketing.domain.User;

import com.ticketing.dto.UserDTO;
import com.ticketing.service.DepartmentService;
import com.ticketing.service.ProjectService;
import com.ticketing.service.RoleService;
import com.ticketing.service.UserService;


@Controller
@RequestMapping("/api/admin/users")
@SessionAttributes("user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private ProjectService projectService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        Optional<User> userOptional = userService.findById(userId);

        if (!userOptional.isPresent()) {
            // Return 404 Not Found if user is not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId);
        }

        User user = userOptional.get();

        // Map the User entity to UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getUserId()); // Use setId as per your DTO
        userDTO.setUserName(user.getUserName());
        userDTO.setEmail(user.getEmail());
//        userDTO.setPassword(user.getUserPassword()); // Include password if your DTO needs it for the form

        // Map Department ID
        if (user.getDepartment() != null) {
            userDTO.setDepartment(user.getDepartment().getDepartmentId());
        }

        // Map Project ID
        if (user.getProject() != null) {
            userDTO.setProject(user.getProject().getProjectId());
        }

        // Map Manager ID
        if (user.getManager() != null) {
            userDTO.setManager(user.getManager().getUserId());
        }

        // Map Roles (Set<Role> from entity to Set<Long> in DTO)
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Long> roleIds = user.getRoles().stream()
                    .map(Role::getRoleId) // Get only the Role ID
                    .collect(Collectors.toSet()); // Collect into a Set<Long>
            userDTO.setRoles(roleIds);
        }

        return ResponseEntity.ok(userDTO); // Return 200 OK with the UserDTO
    }
    
    @GetMapping("/userName/{userName}")
    public ResponseEntity<UserDTO> getUserByUserNameDto(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        if (user != null) {
            UserDTO userDTO = new UserDTO(user.getUserId(), user.getUserName(), user.getEmail());
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<User>> listUsers() {
     List<User> users = userService.findAll();
     return ResponseEntity.ok(users);
    }
   
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<ApiResponse> saveUser(@RequestBody UserDTO userUpdateRequest) {
        try {
            User user;
            if (userUpdateRequest.getId() != null) {
                user = userService.findById(userUpdateRequest.getId()).orElseThrow(() -> new RuntimeException("User not found"));
                user.setUserName(userUpdateRequest.getUserName());
                user.setEmail(userUpdateRequest.getEmail());
            } else {
                user = new User();
                user.setUserName(userUpdateRequest.getUserName());
                user.setEmail(userUpdateRequest.getEmail());

                if (userUpdateRequest.getPassword() == null || userUpdateRequest.getPassword().isEmpty()) {
                    return ResponseEntity.badRequest().body(new ApiResponse(false, "Password is required for new users."));
                }
                user.setUserPassword(new BCryptPasswordEncoder().encode(userUpdateRequest.getPassword()));
            }

            // Handle Manager
            if (userUpdateRequest.getManager() != null) {
                userService.findById(userUpdateRequest.getManager())
                        .ifPresentOrElse(user::setManager, () -> {
                            throw new RuntimeException("Manager not found with ID: " + userUpdateRequest.getManager());
                        });
            } else {
                user.setManager(null); // Allow setting manager to null
            }

            // Handle Department
            if (userUpdateRequest.getDepartment() != null) {
                departmentService.findById(userUpdateRequest.getDepartment())
                        .ifPresentOrElse(user::setDepartment, () -> {
                            throw new RuntimeException("Department not found with ID: " + userUpdateRequest.getDepartment());
                        });
            } else {
                user.setDepartment(null); // Allow setting department to null
            }

            // Handle Project
            if (userUpdateRequest.getProject() != null) {
                projectService.findById(userUpdateRequest.getProject())
                        .ifPresentOrElse(user::setProject, () -> {
                            throw new RuntimeException("Project not found with ID: " + userUpdateRequest.getProject());
                        });
            } else {
                user.setProject(null); // Allow setting project to null
            }

            // Handle Roles
            Set<Role> roles = userUpdateRequest.getRoles().stream()
                    .map(roleId -> roleService.findById(roleId)
                            .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);

            userService.save(user);
            return ResponseEntity.ok(new ApiResponse(true, "User saved successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error saving user: " + e.getMessage()));
        }
    }

    @GetMapping("{userId}/delete")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteById(userId);
    }
}