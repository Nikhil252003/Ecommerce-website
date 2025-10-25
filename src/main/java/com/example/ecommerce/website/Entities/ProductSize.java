package com.example.ecommerce.website.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // e.g., S, M, L, XL
    private int quantity; // stock count for this size

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference  // 👈 pairs with Product.size
    private Product product;
}
