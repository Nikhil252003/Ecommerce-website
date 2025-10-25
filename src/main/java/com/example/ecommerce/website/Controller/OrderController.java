package com.example.ecommerce.website.Controller;

import com.example.ecommerce.website.Entities.Cart;
import com.example.ecommerce.website.Entities.Order;
import com.example.ecommerce.website.Services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Order> userOrders = orderService.findUserOrders(userDetails.getUsername());
        return new ResponseEntity<>(userOrders, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Order> createOrderFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                                     @RequestBody Cart cart) {
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        try {
            Order newOrder = orderService.createOrderFromCart(userDetails.getUsername(), cart);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
