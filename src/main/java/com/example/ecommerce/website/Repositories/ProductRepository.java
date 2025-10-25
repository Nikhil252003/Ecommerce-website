package com.example.ecommerce.website.Repositories;

import com.example.ecommerce.website.Entities.Category;
import com.example.ecommerce.website.Entities.Product;

import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;
    
    import java.util.List;
    
    
    /**
     * The ProductRepository interface provides CRUD (Create, Read, Update, Delete)
     * operations for the Product entity. Spring Data JPA automatically provides
     * the implementation at runtime.
     *
     * It extends JpaRepository, specifying the entity type (Product) and the ID type (UUID).
     */
    @Repository
    public interface ProductRepository extends JpaRepository<Product, Long> {
    
        /**
         * Finds products by their category. This is a custom query method.
         * Spring Data JPA can automatically generate the implementation for this method
         * by parsing its name.
         * @param category The category to search for.
         * @return A list of products belonging to the given category.
         */
        List<Product> findByCategory(Category category);
        List<Product> findByCategoryId(Long categoryId);

    }
    
    


