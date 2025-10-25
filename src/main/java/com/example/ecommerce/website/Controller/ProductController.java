package com.example.ecommerce.website.Controller;



import com.example.ecommerce.website.Entities.Product;
import com.example.ecommerce.website.Repositories.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;



import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
// Public endpoint to fetch products without login
@GetMapping("/public")
public ResponseEntity<List<Product>> getAllProductsPublic() {
    List<Product> products = productRepository.findAll();
    return ResponseEntity.ok(products);
}

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(@AuthenticationPrincipal UserDetails userDetails) {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        product.setSize(product.getSize()); 
        Product newProduct = productRepository.save(product);
        return ResponseEntity.ok(newProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product existingProduct = product.get();
           existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setBrand(updatedProduct.getBrand());
        existingProduct.setColor(updatedProduct.getColor());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDiscountedPrice(updatedProduct.getDiscountedPrice());
        existingProduct.setDiscountPersent(updatedProduct.getDiscountPersent());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        existingProduct.setTopLavelCategory(updatedProduct.getTopLavelCategory());
        existingProduct.setSecondLavelCategory(updatedProduct.getSecondLavelCategory());
        existingProduct.setThirdLavelCategory(updatedProduct.getThirdLavelCategory());
        existingProduct.setCategory(updatedProduct.getCategory());

        // ✅ Update size list (clear old, set new, maintain bidirectional relation)
        existingProduct.getSize().clear();
        if (updatedProduct.getSize() != null) {
            updatedProduct.getSize().forEach(size -> size.setProduct(existingProduct));
            existingProduct.getSize().addAll(updatedProduct.getSize());
        }
            Product savedProduct = productRepository.save(existingProduct);
            return ResponseEntity.ok(savedProduct);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
