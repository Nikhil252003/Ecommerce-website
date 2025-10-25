package com.example.ecommerce.website.Repositories;


    import com.example.ecommerce.website.Entities.CartItem;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;
    
    import java.util.UUID;
    
    /**
     * The CartItemRepository interface provides CRUD operations for the CartItem entity.
     * Spring Data JPA automatically provides the implementation at runtime.
     *
     * It extends JpaRepository, specifying the entity type (CartItem) and the ID type (UUID).
     */
    @Repository
    public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    
    }
    
    



