package com.ticketing.dto;

import java.util.Objects; // Add this import

public class RoleDTO {

    private Long roleId;
    private String roleName;

    public RoleDTO() {
    }

    public RoleDTO(Long roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

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
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleDTO that = (RoleDTO) o;
        return Objects.equals(roleName, that.roleName); // Compare based on roleName
        // You might want to include roleId in the comparison as well, depending on your needs:
        // return Objects.equals(roleId, that.roleId) && Objects.equals(roleName, that.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName); // Consistent with the equals method
        // If you include roleId in equals, include it here too:
        // return Objects.hash(roleId, roleName);
    }

    // toString() method (optional but helpful for debugging)
    @Override
    public String toString() {
        return "RoleResponse{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
//                ", empty=" + empty +
                '}';
    }
}