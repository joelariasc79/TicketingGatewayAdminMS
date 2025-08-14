package com.ticketing.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut; 
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketing.domain.User;
import com.ticketing.repository.UserRepository;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
    private UserRepository userRepository;

    /**
     * Saves or updates a User.
     * Updates the individual User in the "users" cache and evicts all relevant list caches.
     * Also evicts the "userDetails" cache, as user details (password, roles) might change.
     *
     * @param user The User entity to save.
     * @return The saved User entity.
     */
    @Override
    @CachePut(value = "users", key = "#user.userId") // Update the specific user by their ID
    @Caching(evict = { // <--- Use @Caching with 'evict' attribute
        @CacheEvict(value = {"allUsers", "usersByDepartmentAndProject"}, allEntries = true), // Evict list caches
        @CacheEvict(value = "userDetails", key = "#user.userName") // Evict specific user's details
    })
    public User save(User user) {
        System.out.println("Saving/Updating User: " + user.getUserName() + " in DB and refreshing caches.");
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public Optional<User> enableUser(Long userId) {
        System.out.println("Attempting to enable User with ID: " + userId); // For demonstration
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isEnabled()) { // Only enable if currently disabled
                user.setEnabled(true);
                // Call save() method, which has @CachePut and @Caching(evict=...)
                // This ensures the single user cache is updated and list caches are cleared.
                User updatedUser = save(user);
                System.out.println("User " + userId + " enabled and cache updated/evicted.");
                return Optional.of(updatedUser);
            }
        }
        System.out.println("User " + userId + " not found or already enabled. No action taken.");
        return Optional.empty();
    }
    
    @Override
    @Transactional
    public Optional<User> disableUser(Long userId) {
        System.out.println("Attempting to disable User with ID: " + userId); // For demonstration
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.isEnabled()) { // Only disable if currently enabled
                user.setEnabled(false);
                // Call save() method, which has @CachePut and @Caching(evict=...)
                // This ensures the single user cache is updated and list caches are cleared.
                User updatedUser = save(user);
                System.out.println("User " + userId + " disabled and cache updated/evicted.");
                return Optional.of(updatedUser);
            }
        }
        System.out.println("User " + userId + " not found or already disabled. No action taken.");
        return Optional.empty();
    }

    /**
     * Finds a User by their ID.
     * Caches the individual User object.
     *
     * @param id The ID of the user.
     * @return An Optional containing the User if found, otherwise empty.
     */
    @Override
    @Cacheable(value = "users", key = "#userId")
    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        System.out.println("Fetching User by ID: " + userId + " from DB or Cache.");
        return userRepository.findById(userId);
    }

    /**
     * Retrieves all User entities.
     * Caches the entire list of users.
     *
     * @return A list of all User entities.
     */
    @Override
    @Cacheable(value = "allUsers")
    @Transactional(readOnly = true)
    public List<User> findAll() {
        System.out.println("Fetching all Users from DB or Cache.");
        return userRepository.findAll();
    }

    /**
     * Deletes a User by their ID.
     * Evicts the individual User from the "users" cache and all relevant list caches.
     * Also evicts the "userDetails" cache (requires knowing the username).
     *
     * @param id The ID of the user to delete.
     */
    @Override
    @Caching(evict = { // <--- Use @Caching with 'evict' attribute
        @CacheEvict(value = {"users", "allUsers", "usersByDepartmentAndProject"}, allEntries = true), // Clear relevant list caches
        // Note: For deleteById, if you want to be precise for userDetails,
        // you'd typically fetch the user first to get their username.
        // As a fallback, clearing all userDetails entries is safer if username isn't available.
        @CacheEvict(value = "userDetails", allEntries = true)
    })
    public void deleteById(Long id) {
        System.out.println("Deleting User by ID: " + id + " from DB and evicting caches.");
        userRepository.deleteById(id);
    }

    /**
     * Finds a User by their username.
     * Caches the individual User object. This cache is also used by UserDetailServiceImpl.
     *
     * @param userName The username of the user.
     * @return The User entity if found, otherwise null.
     */
    @Override
    @Cacheable(value = "users", key = "#userName") // Cache by username
    @Transactional(readOnly = true)
    public User findByUserName(String userName) {
        System.out.println("Fetching User by username: " + userName + " from DB or Cache.");
        return userRepository.findByUserName(userName);
    }

    /**
     * Finds users by their Department and Project IDs.
     * Caches the list of users for specific department and project combinations.
     *
     * @param departmentId The ID of the department.
     * @param projectId The ID of the project.
     * @return A list of User entities.
     */
    @Override
    @Cacheable(value = "usersByDepartmentAndProject", key = "#departmentId + '-' + #projectId")
    @Transactional(readOnly = true)
    public List<User> findUsersByDepartmentAndProject(Long departmentId, Long projectId) {
        System.out.println("Finding users by Department ID: " + departmentId + " and Project ID: " + projectId + " from DB or Cache.");
        if (departmentId != null && projectId != null) {
            return userRepository.findByDepartment_DepartmentIdAndProject_ProjectId(departmentId, projectId);
        } else if (departmentId != null) {
            return userRepository.findByDepartment_DepartmentId(departmentId);
        } else if (projectId != null) {
            return userRepository.findByProject_ProjectId(projectId);
        } else {
            return userRepository.findAll();
        }
    } 
    
    /**
     * Deletes a User entity.
     * Evicts the individual User from the "users" cache and all relevant list caches.
     * Also evicts the "userDetails" cache.
     *
     * @param user The User entity to delete.
     */
    @Override
    @Caching(evict = { // <--- Use @Caching with 'evict' attribute
        @CacheEvict(value = {"users", "allUsers", "usersByDepartmentAndProject"}, allEntries = true), // Clear all list caches
        @CacheEvict(value = "userDetails", key = "#user.userName") // Clear specific userDetails entry
    })
    public void delete(User user) {
        System.out.println("Deleting User: " + user.getUserName() + " from DB and evicting caches.");
        userRepository.delete(user);
    }
    
    
//    @CacheEvict(value = "users", key = "#userId") 
//    @Transactional
//    public Optional<User> enableUser(Long userId) {
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            if (!user.isEnabled()) { // Only enable if currently disabled
//                user.setEnabled(true);
//                return Optional.of(userRepository.save(user));
//            }
//        }
//        return Optional.empty();
//    }
//    
//    @CacheEvict(value = "users", key = "#userId")
//    @Transactional
//    public Optional<User> disableUser(Long userId) {
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            if (user.isEnabled()) { // Only disable if currently enabled
//                user.setEnabled(false);
//                return Optional.of(userRepository.save(user));
//            }
//        }
//        return Optional.empty();
//    }

}
