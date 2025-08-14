package com.ticketing.dto;

import com.ticketing.domain.User; // Import the User domain entity
import com.ticketing.domain.Role; // Import Role entity
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDto {

    private Long id;
    private String userName;
    private String email;
    private boolean enabled;
    private String password; // Only used for incoming requests (e.g., signup/save new user)
    private Long manager;
    private Long department;
    private Long project;
    private Set<RoleDto> roles; // CHANGED: Now a Set of RoleDto objects
    // private Set<String> roleNames; // REMOVED: Redundant as roleName is in RoleDto

    // Default Constructor
    public UserDto() {
    }  
    
    // Delete later
//   	public UserDto(Long id, String userName, String email, String password, Long manager, Long department,
//   			Long project, Set<RoleDto> roles) {
//   		super();
//   		this.id = id;
//   		this.userName = userName;
//   		this.email = email;
//   		this.password = password;
//   		this.manager = manager;
//   		this.department = department;
//   		this.project = project;
//   		this.roles = roles;
//   	}
    
    // Constructor for ID and Username (often used for simple display/selection)
    public UserDto(Long id, String userName) {
		this.id = id;
		this.userName = userName;
	}
    
    // Constructor for basic user details including enabled status
    public UserDto(Long id, String userName, String email, boolean enabled) {
		this.id = id;
		this.userName = userName;
		this.email = email;
	}
    
    // Comprehensive constructor for incoming DTOs (e.g., from client for save/update)
    // This constructor now expects Set<Long> for role IDs for input, and converts them to RoleDto shells.
    // If your client sends Set<RoleDto>, you'd need another constructor or mapping logic.
    public UserDto(Long id, String userName, String email, boolean enabled, String password, Long manager,
			Long department, Long project, Set<Long> roleIds) { // Parameter renamed for clarity
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.enabled = enabled;
		this.password = password;
		this.manager = manager;
		this.department = department;
		this.project = project;
		// When constructing from input IDs, we create RoleDto objects with just the ID
		this.roles = roleIds != null
		             ? roleIds.stream().map(roleId -> {
		                 RoleDto dto = new RoleDto();
		                 dto.setRoleId(roleId);
		                 // roleName will be null here as it's not provided in input
		                 return dto;
		             }).collect(Collectors.toSet())
		             : new HashSet<>();
	}
    
    // Check reset password (Simple constructor, `roles` field will be null)
    public UserDto(Long id, String userName, String email) {
		this.id = id;
		this.userName = userName;
		this.email = email;
	}

    // Constructor for converting a User entity to a UserDto
    // This constructor is used by the UserListController to build the response DTO
    public UserDto(User user) {
        this.id = user.getUserId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.enabled = user.isEnabled();
        this.password = null; // Password is not returned in DTO

        // Map related entities to their IDs
        this.manager = user.getManager() != null ? user.getManager().getUserId() : null;
        this.department = user.getDepartment() != null ? user.getDepartment().getDepartmentId() : null;
        this.project = user.getProject() != null ? user.getProject().getProjectId() : null;

        // CHANGED: Map Role entities to RoleDto objects for the 'roles' field
        this.roles = user.getRoles() != null
                     ? user.getRoles().stream().map(RoleDto::new).collect(Collectors.toSet()) // Use RoleDto(Role role) constructor
                     : new HashSet<>();

        // REMOVED: roleNames field is now redundant
    }

    // Getters and Setters

	public Long getId() {
        return id;
    }
	
	public Long getUserId() { // Keeping for convenience/backward compatibility
        return id;
    }
	
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public Long getManager() {
		return manager;
	}

	public void setManager(Long manager) {
		this.manager = manager;
	}

	public Long getDepartment() {
		return department;
	}

	public void setDepartment(Long department) {
		this.department = department;
	}

	public Long getProject() {
		return project;
	}

	public void setProject(Long project) {
		this.project = project;
	}
	
	// CHANGED: Getter and Setter for roles now use Set<RoleDto>
	public Set<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDto> roles) {
        this.roles = roles;
    }

    // REMOVED: Getter and Setter for roleNames (it's no longer a field)
    // public Set<String> getRoleNames() {
    //     return roleNames;
    // }
    // public void setRoleNames(Set<String> roleNames) {
    //     this.roleNames = roleNames;
    // }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", password='********'" + // Masking password for toString
                ", manager=" + manager +
                ", department=" + department +
                ", project=" + project +
                ", roles=" + roles + // Now will display RoleDto objects
                '}';
    }
}




//package com.ticketing.dto;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import com.ticketing.domain.User;
//
//public class UserDto {
//
//    private Long id;
//    private String userName;
//    private String email;
//    private boolean enabled;
//    private String password;
//    private Long manager;
//    private Long department;
//    private Long project;
//    private Set<Long> roles; // Assuming you're passing Role IDs
//
//    // Default Constructor
//    public UserDto() {
//    }    
//    
//    public UserDto(Long id, String userName) {
//		super();
//		this.id = id;
//		this.userName = userName;
//	}
//    
//
//    public UserDto(Long id, String userName, String email, boolean enabled, String password, Long manager,
//			Long department, Long project, Set<Long> roles) {
//		super();
//		this.id = id;
//		this.userName = userName;
//		this.email = email;
//		this.enabled = enabled;
//		this.password = password;
//		this.manager = manager;
//		this.department = department;
//		this.project = project;
//		this.roles = roles;
//	}
//
//    // Delete later
//	public UserDto(Long id, String userName, String email, String password, Long manager, Long department,
//			Long project, Set<Long> roles) {
//		super();
//		this.id = id;
//		this.userName = userName;
//		this.email = email;
//		this.password = password;
//		this.manager = manager;
//		this.department = department;
//		this.project = project;
//		this.roles = roles;
//	}
//    
//    public UserDto(Long id, String userName, String email, boolean enabled) {
//		super();
//		this.id = id;
//		this.userName = userName;
//		this.email = email;
//		this.enabled = enabled;
//	}
//    
//    // delete later, check reset password
//    public UserDto(Long id, String userName, String email) {
//		super();
//		this.id = id;
//		this.userName = userName;
//		this.email = email;
//	}
//    
//    public UserDto(User user) {
//        this.id = user.getUserId();
//        this.userName = user.getUserName();
//        this.email = user.getEmail();
//        this.enabled = user.isEnabled();
//        // Password is not typically included when converting from entity to DTO for security reasons
//        this.password = null; // Or you could choose to omit this field from the DTO entirely if only used for input
//
//        // Map related entities to their IDs
//        this.manager = user.getManager() != null ? user.getManager().getUserId() : null;
//        this.department = user.getDepartment() != null ? user.getDepartment().getDepartmentId() : null;
//        this.project = user.getProject() != null ? user.getProject().getProjectId() : null;
//
//        // Map roles to their IDs
//        this.roles = user.getRoles() != null
//                     ? user.getRoles().stream().map(role -> role.getRoleId()).collect(Collectors.toSet())
//                     : new HashSet<>();
//    }
//    
// // Getters and Setters
//
//	public Long getId() {
//        return id;
//    }
//	
//	public Long getUserId() {
//        return id;
//    }
//	
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//    
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public boolean isEnabled() {
//		return enabled;
//	}
//
//	public void setEnabled(boolean enabled) {
//		this.enabled = enabled;
//	}
//
//	public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//    
//    public Long getManager() {
//		return manager;
//	}
//
//	public void setManager(Long manager) {
//		this.manager = manager;
//	}
//
//	public Long getDepartment() {
//		return department;
//	}
//
//	public void setDepartment(Long department) {
//		this.department = department;
//	}
//
//	public Long getProject() {
//		return project;
//	}
//
//	public void setProject(Long project) {
//		this.project = project;
//	}
//	
//	public Set<Long> getRoles() {
//        return roles;
//    }
//
//    public void setRoles(Set<Long> roles) {
//        this.roles = roles;
//    }
//
//    @Override
//    public String toString() {
//        return "UserDto{" + // Changed from RoleDTO to UserDto for consistency
//                "id=" + id +
//                ", userName='" + userName + '\'' +
//                ", email='" + email + '\'' +
//                ", enabled=" + enabled + // Include enabled status in toString
//                ", password='********'" + // Masking password for toString
//                ", manager=" + manager +
//                ", department=" + department +
//                ", project=" + project +
//                ", roles=" + roles +
//                '}';
//    }
//}