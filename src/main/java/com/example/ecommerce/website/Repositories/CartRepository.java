package com.example.ecommerce.website.Repositories;


import com.example.ecommerce.website.Entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * The CartRepository interface provides CRUD operations for the Cart entity.
 * Spring Data JPA automatically provides the implementation at runtime.
 *
 * It extends JpaRepository, specifying the entity type (Cart) and the ID type (UUID).
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

}



