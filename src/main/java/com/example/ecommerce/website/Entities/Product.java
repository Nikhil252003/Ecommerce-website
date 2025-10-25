package com.example.ecommerce.website.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String brand;
    private String color;
    private double price;
    private double discountedPrice;
    private int discountPersent;
    private int quantity;
    private String imageUrl;

    private String topLavelCategory;
    private String secondLavelCategory;
    private String thirdLavelCategory;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore   // 👈 ignore category in JSON to avoid recursion
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // 👈 owns the relationship with ProductSize
    private List<ProductSize> size;

    // Helper method to maintain bidirectional relationship
    public void setSize(List<ProductSize> size) {
        this.size = size;
        if (size != null) {
            size.forEach(s -> s.setProduct(this));
        }
    }
}
