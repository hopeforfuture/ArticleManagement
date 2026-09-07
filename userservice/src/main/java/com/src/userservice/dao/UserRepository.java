package com.src.userservice.dao;
import com.src.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email
    Optional<User> findByEmail(String email);

    // Check whether email already exists
    boolean existsByEmail(String email);

    // Find users by status
    List<User> findByStatus(Integer status);

    // Find user by email and status
    Optional<User> findByEmailAndStatus(String email, Integer status);

    // Find users by role
    List<User> findByRole(String role);

    // Find users by role and status
    List<User> findByRoleAndStatus(String role, Integer status);

    // Search users by name
    List<User> findByNameContainingIgnoreCase(String name);

    // Find active users by name
    List<User> findByNameContainingIgnoreCaseAndStatus(
            String name,
            Integer status
    );
}