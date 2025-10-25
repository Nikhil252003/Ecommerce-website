package com.example.ecommerce.website.Repositories;

import com.example.ecommerce.website.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



/**
 * The CategoryRepository interface provides CRUD (Create, Read, Update, Delete)
 * operations for the Category entity. Spring Data JPA automatically provides
 * the implementation at runtime.
 *
 * It extends JpaRepository, specifying the entity type (Category) and the ID type (UUID).
 */
@Repository
public interface Categoryrepository extends JpaRepository<Category, Long> {

}

    

