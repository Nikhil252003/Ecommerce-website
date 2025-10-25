package com.example.ecommerce.website.Services;

import com.example.ecommerce.website.Entities.Cart;
import com.example.ecommerce.website.Entities.Order;
import com.example.ecommerce.website.Entities.OrderItem;
import com.example.ecommerce.website.Entities.User;
import com.example.ecommerce.website.Repositories.OrderRepository;
import com.example.ecommerce.website.Repositories.UserRepository;
import com.example.ecommerce.website.Repositories.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public List<Order> findUserOrders(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return orderRepository.findByUserId(user.getId());
        }
        return List.of();
    }
    
    // ✅ New method to create an order from a cart
    @Transactional
    public Order createOrderFromCart(String username, Cart cart) {
        // 1. Find the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Create the new Order entity
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setOrderStatus("Pending"); // Set initial status
        
        // 3. Populate OrderItems from CartItems and calculate total
        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setOrder(newOrder);
                    return orderItem;
                }).toList();
        
        newOrder.setOrderItems(orderItems);
        
        double total = orderItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        newOrder.setTotalAmount(total);

        // 4. Save the new order to the database
        Order savedOrder = orderRepository.save(newOrder);

        // 5. Optionally, clear the user's cart after the order is placed
        cartItemRepository.deleteAll(cart.getCartItems());

        return savedOrder;
    }
}