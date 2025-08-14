
package com.ticketing.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Good practice for read-only methods

import com.ticketing.domain.User;

@Service
public interface UserService {

    // --- Caching Strategy for User Operations ---
    // Cache Name for individual User objects: "users"
    // Cache Name for lists of Users: "allUsers", "usersByDepartmentAndProject"
    // Cache Name for specific user details by username (for UserDetailsService): "userDetails"

    /**
     * Saves or updates a User.
     * Updates the individual User in the "users" cache and evicts all relevant list caches.
     * Also evicts the "userDetails" cache, as user details (password, roles) might change.
     *
     * @param user The User entity to save.
     * @return The saved User entity.
     */
    @CachePut(value = "users", key = "#user.userId") // Update the specific user by their ID
    @CacheEvict(value = {
            "allUsers",                   // General list of users
            "usersByDepartmentAndProject", // If department/project changes
            "userDetails"                 // UserDetails object needs to be fresh for Spring Security
        }, allEntries = true) // Evict all entries from these list/detail caches
    User save(User user);
    
    
    Optional<User> enableUser(Long userId);
    Optional<User> disableUser(Long userId);

    /**
     * Finds a User by their ID.
     * Caches the individual User object.
     *
     * @param id The ID of the user.
     * @return An Optional containing the User if found, otherwise empty.
     */
    @Cacheable(value = "users", key = "#userId")
    @Transactional(readOnly = true)
    Optional<User> findById(Long userId);
    
    /**
     * Retrieves all User entities.
     * Caches the entire list of users.
     *
     * @return A list of all User entities.
     */
    @Cacheable(value = "allUsers")
    @Transactional(readOnly = true)
    List<User> findAll();

    /**
     * Deletes a User entity.
     * Evicts the individual User from the "users" cache and all relevant list caches.
     * Also evicts the "userDetails" cache.
     *
     * @param user The User entity to delete.
     */
    @CacheEvict(value = {
            "users",                      // Individual user entry
            "allUsers",                   // General list
            "usersByDepartmentAndProject", // If this user was in a specific combo
            "userDetails"                 // UserDetails object must be removed
        }, key = "#user.userName", allEntries = true) // Use username for userDetails eviction; allEntries for others.
                                                    // Note: Combining key and allEntries works differently based on specific cache manager.
                                                    // A safer approach might be separate @CacheEvict annotations for clarity,
                                                    // but this is often used for broad invalidation.
    void delete(User user);

    /**
     * Deletes a User by their ID.
     * Evicts the individual User from the "users" cache and all relevant list caches.
     * Also evicts the "userDetails" cache (requires knowing the username).
     *
     * @param id The ID of the user to delete.
     */
    @CacheEvict(value = {
            "users",
            "allUsers",
            "usersByDepartmentAndProject",
            "userDetails" // This will clear all userDetails if allEntries=true.
                          // If you want to clear only by ID, you'd need to fetch user first.
        }, allEntries = true) // Simplified to allEntries for broad eviction upon delete
    void deleteById(Long id);

    /**
     * Finds a User by their username.
     * Caches the individual User object. This cache is also used by UserDetailServiceImpl.
     *
     * @param userName The username of the user.
     * @return The User entity if found, otherwise null.
     */
    @Cacheable(value = "users", key = "#userName") // Cache by username
    @Transactional(readOnly = true)
    User findByUserName(String userName);

    /**
     * Finds users by their Department and Project IDs.
     * Caches the list of users for specific department and project combinations.
     *
     * @param departmentId The ID of the department.
     * @param projectId The ID of the project.
     * @return A list of User entities.
     */
    @Cacheable(value = "usersByDepartmentAndProject", key = "#departmentId + '-' + #projectId")
    @Transactional(readOnly = true)
    List<User> findUsersByDepartmentAndProject(Long departmentId, Long projectId);
    
    
//    @CacheEvict(value = "users", key = "#userId")
//    @Transactional
//    public Optional<User> enableUser(Long userId);
    
//    @CacheEvict(value = "users", key = "#userId")
//    @Transactional
//    public Optional<User> disableUser(Long userId);
}
