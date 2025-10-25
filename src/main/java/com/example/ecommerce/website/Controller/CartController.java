package com.example.ecommerce.website.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.example.ecommerce.website.Entities.Cart;
import com.example.ecommerce.website.Entities.CartItem;
import com.example.ecommerce.website.Entities.Product;
import com.example.ecommerce.website.Entities.User;
import com.example.ecommerce.website.Repositories.CartRepository;
import com.example.ecommerce.website.Repositories.CartItemRepository;
import com.example.ecommerce.website.Repositories.UserRepository;
import com.example.ecommerce.website.Repositories.ProductRepository;

@RestController
@RequestMapping("/cart") // Consider changing this to "/api/cart" to match your frontend
public class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository; // <-- Added this field
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartController(CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository; // <-- Initialized it here
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Get current user's cart
    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return userRepository.findByUsername(userDetails.getUsername())
                .map(user -> ResponseEntity.ok(user.getCart()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Add product to cart
    @PostMapping("/add/{productId}")
    @Transactional
    public ResponseEntity<Cart> addProductToCart(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable Long productId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> userOpt = userRepository.findByUsername(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userOpt.get();
        Cart cart = user.getCart();

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem item = existingCartItem.get();
            item.setQuantity(item.getQuantity() + 1);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(productOpt.get());
            cartItem.setQuantity(1);
            cart.getCartItems().add(cartItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return ResponseEntity.ok(savedCart);
    }
    
    // Update cart item quantity using the cart item ID
    @PutMapping("/update/{itemId}")
    @Transactional
    public ResponseEntity<Cart> updateCartItemQuantity(@AuthenticationPrincipal UserDetails userDetails,
                                                       @PathVariable UUID itemId, 
                                                       @RequestBody Map<String, Integer> payload) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<CartItem> itemOpt = cartItemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CartItem item = itemOpt.get();
        if (!item.getCart().getUser().getUsername().equals(userDetails.getUsername())) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        int newQuantity = payload.get("quantity");
        if (newQuantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        }

        Optional<User> userOpt = userRepository.findByUsername(userDetails.getUsername());
        return userOpt.map(user -> ResponseEntity.ok(user.getCart()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Remove cart item using the cart item ID
    @DeleteMapping("/remove/{itemId}")
    @Transactional
    public ResponseEntity<Cart> removeCartItem(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable UUID itemId) { 
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<CartItem> itemOpt = cartItemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CartItem item = itemOpt.get();
        if (!item.getCart().getUser().getUsername().equals(userDetails.getUsername())) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        cartItemRepository.delete(item);

        Optional<User> userOpt = userRepository.findByUsername(userDetails.getUsername());
        return userOpt.map(user -> ResponseEntity.ok(user.getCart()))
                .orElse(ResponseEntity.notFound().build());
    }
}