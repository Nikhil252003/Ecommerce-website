package com.example.ecommerce.website.Entities;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * The Category entity class represents a product category.
 * It maps to the 'category' table in the database.
 *
 * @Entity marks this class as a JPA entity.
 *
 * @Data provides getters, setters, toString, equals, and hashCode methods.
 *
 * @NoArgsConstructor and @AllArgsConstructor generate constructors.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // The unique identifier for each category.
    private String name; // The name of the category (e.g., "Men," "Women," "Accessories").
}
    

