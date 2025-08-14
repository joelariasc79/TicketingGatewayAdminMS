package com.ticketing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ticketing.domain.User;
import com.ticketing.dto.UserDto;
import com.ticketing.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/api/v1/admin/users/resetPassword")
@SessionAttributes("user")
public class ResetPasswordController {

    @Autowired
    private UserService userService;
    
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordController.class);
    
    
    @GetMapping("/filter")
    public ResponseEntity<List<UserDto>> findUsersByDepartmentAndProject(
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "projectId", required = false) Long projectId) {

        List<User> users = userService.findUsersByDepartmentAndProject(departmentId, projectId);

        // Map User entities to UserDTOs
        List<UserDto> userDTOs = users.stream()
                                      .map(user -> new UserDto(user.getUserId(), user.getUserName(), user.getEmail()))
                                      .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs); // Return the DTOs with a 200 OK status
    }
    
    
    @GetMapping("/{userId}")
    public String resetPasswordForm(@PathVariable Long userId, Model model) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        model.addAttribute("formHeading", "Edit User");
        model.addAttribute("userFormTitle", "Edit User");
        model.addAttribute("userId", user.getUserId());
        
        return "resetPassword"; // Returns the Thymeleaf template named "signup.html"
    }
    
    @PostMapping("/save")
    public String updatePassword(@ModelAttribute UserDto userUpdateRequest, RedirectAttributes redirectAttributes) {
        try {
            // Ensure the user ID is provided for updating
            if (userUpdateRequest.getUserId() == null) {
                redirectAttributes.addFlashAttribute("error", "User ID is required for updating the password.");
                return "redirect:http://localhost:8181/resetPassword/resetPassword?id=" + userUpdateRequest.getUserId(); // Redirect back to the frontend
            }

            // Ensure a new password is provided
            if (userUpdateRequest.getPassword() == null || userUpdateRequest.getPassword().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "New password is required.");
                return "redirect:http://localhost:8181/resetPassword/resetPassword?id=" + userUpdateRequest.getUserId(); // Redirect back to the frontend
            }

//            // Implement later
//            // Ensure the confirm password matches
//            if (!userUpdateRequest.getPassword().equals(userUpdateRequest.getConfirmPassword())) {
//                redirectAttributes.addFlashAttribute("error", "New password and confirm password do not match.");
//                return "redirect:http://localhost:8181/resetPassword?id=" + userUpdateRequest.getId(); // Redirect back to the frontend
//            }
            
            User user = userService.findById(userUpdateRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userUpdateRequest.getUserId()));

            // Encode the new password
            user.setUserPassword(new BCryptPasswordEncoder().encode(userUpdateRequest.getPassword()));

            // Save the updated user
            userService.save(user);

            redirectAttributes.addFlashAttribute("success", "Password updated successfully. Please log in.");
            return "redirect:http://localhost:8181/login"; // Redirect to the frontend login page

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating password: " + e.getMessage());
            return "redirect:http://localhost:8181/resetPassword/resetPassword?id=" + userUpdateRequest.getUserId(); // Redirect back to the frontend
        }
    }

}