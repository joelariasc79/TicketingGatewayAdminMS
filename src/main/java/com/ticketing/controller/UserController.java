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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.ticketing.domain.User;
import com.ticketing.dto.ApiResponse;
import com.ticketing.dto.RoleDto;
import com.ticketing.dto.UserDto;
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
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        Optional<User> userOptional = userService.findById(userId);

        if (!userOptional.isPresent()) {
            // Return 404 Not Found if user is not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId);
        }

        User user = userOptional.get();
        
        System.out.println("user: " + user.getUserId());

        // Map the User entity to UserDTO
        UserDto userDTO = new UserDto();
        userDTO.setId(user.getUserId()); // Use setId as per your DTO
        userDTO.setUserName(user.getUserName());
        userDTO.setEmail(user.getEmail());
        userDTO.setEnabled(user.isEnabled());
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

        	Set<RoleDto> roleDtos = user.getRoles().stream()
        	          .map(role -> new RoleDto(role)) // Map each Role to a new RoleDto object
        	          .collect(Collectors.toSet());
          userDTO.setRoles(roleDtos);
        	
        }

        return ResponseEntity.ok(userDTO); // Return 200 OK with the UserDTO
    }
    
    @GetMapping("/userName/{userName}")
    public ResponseEntity<UserDto> getUserByUserNameDto(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        if (user != null) {
            UserDto userDTO = new UserDto(user.getUserId(), user.getUserName(), user.getEmail(), user.isEnabled());
            
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> userDtos = userService.findAll().stream()
                                            .map(UserDto::new) // Convert each User entity to UserDto
                                            .collect(Collectors.toList());
            
            
            System.out.println("userDtos: " + userDtos);
            return new ResponseEntity<>(userDtos, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception for debugging purposes
            System.err.println("Error fetching all users: " + e.getMessage());
            // Return an appropriate error response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<ApiResponse> saveUser(@RequestBody UserDto userUpdateRequest) {
        try {
            User user;
            if (userUpdateRequest.getId() != null) {
                // Updating existing user
                user = userService.findById(userUpdateRequest.getId())
                                  .orElseThrow(() -> new RuntimeException("User not found"));
                user.setUserName(userUpdateRequest.getUserName());
                user.setEmail(userUpdateRequest.getEmail());
                // When updating, 'enabled' status might be changed via specific enable/disable endpoints
                // or explicitly in DTO if admin manages it.
                // For this request, if updating, we ensure it remains enabled unless explicitly set otherwise.
                // user.setEnabled(true); // You can uncomment this if you always want updates to default to enabled
            } else {
                // Creating a new user
                user = new User();
                user.setUserName(userUpdateRequest.getUserName());
                user.setEmail(userUpdateRequest.getEmail());

                if (userUpdateRequest.getPassword() == null || userUpdateRequest.getPassword().isEmpty()) {
                    return ResponseEntity.badRequest().body(new ApiResponse(false, "Password is required for new users."));
                }
                user.setUserPassword(new BCryptPasswordEncoder().encode(userUpdateRequest.getPassword()));
                user.setEnabled(true); // NEW: Automatically enable new users
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
            
            Set<RoleDto> roles = userUpdateRequest.getRoles().stream().collect(Collectors.toSet());
            

            userService.save(user); // This will save the user with the enabled status set
            return ResponseEntity.ok(new ApiResponse(true, "User saved successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error saving user: " + e.getMessage()));
        }
    }
    
    
    @PutMapping("/{userId}/disable")
    public ResponseEntity<UserDto> disableUser(@PathVariable Long userId) {
        return userService.disableUser(userId)
                .map(updatedUser -> new ResponseEntity<>(new UserDto(updatedUser), HttpStatus.OK)) // Convert User to UserDto
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PutMapping("/{userId}/enable")
    public ResponseEntity<UserDto> enableUser(@PathVariable Long userId) {
        return userService.enableUser(userId)
                .map(updatedUser -> new ResponseEntity<>(new UserDto(updatedUser), HttpStatus.OK)) // Convert User to UserDto
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @DeleteMapping("/{userId}") // Changed from @GetMapping to @DeleteMapping
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        try {
            // Optional: You might want to check if the user exists before attempting deletion
            // if (userService.findById(userId).isEmpty()) {
            //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found."));
            // }
            userService.deleteById(userId);
            return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Error deleting user: " + e.getMessage()));
        }
    }
}