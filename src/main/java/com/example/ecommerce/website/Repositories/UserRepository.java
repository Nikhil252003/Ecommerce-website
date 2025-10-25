package com.example.ecommerce.website.Repositories;


import com.example.ecommerce.website.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * The UserRepository interface provides CRUD (Create, Read, Update, Delete)
 * operations for the User entity.
 *
 * It extends JpaRepository, specifying the entity type (User) and the ID type (UUID).
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their username. This is a critical method for Spring Security.
     * The framework automatically generates the implementation based on the method name.
     *
     * @param username The username to search for.
     * @return An Optional containing the found user, or an empty Optional if not found.
     */
    Optional<User> findByUsername(String username);
}

    

