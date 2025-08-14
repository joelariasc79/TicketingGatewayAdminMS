package com.ticketing.dto;

import com.ticketing.domain.Role; // Import the domain Role entity
import java.util.Objects; // Already present, just ensuring it's there

public class RoleDto {

    private Long roleId;
    private String roleName;

    // Default constructor
    public RoleDto() {
    }

    // Constructor for creating RoleDto from raw ID and Name (e.g., for direct input or testing)
    public RoleDto(Long roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    // NEW: Constructor for mapping from Role entity (THIS WAS THE MISSING PART)
    // This constructor is called by UserDto(User user) via RoleDto::new
    public RoleDto(Role role) {
        this.roleId = role.getRoleId();
        // Assuming Role.getRoleName() returns an enum and .name() converts it to String
        this.roleName = role.getRoleName() != null ? role.getRoleName().name() : null;
    }


    // Getters and Setters
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    // equals() and hashCode() for proper Set behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleDto that = (RoleDto) o;
        // It's generally best practice to include all significant fields for equality.
        // For a DTO representing a distinct role, both ID and name contribute to uniqueness.
        return Objects.equals(roleId, that.roleId) && Objects.equals(roleName, that.roleName);
    }

    @Override
    public int hashCode() {
        // Consistent with the equals method
        return Objects.hash(roleId, roleName);
    }

    // toString() method (optional but helpful for debugging)
    @Override
    public String toString() {
        return "RoleDto{" + // Changed from RoleResponse to RoleDto for consistency
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
