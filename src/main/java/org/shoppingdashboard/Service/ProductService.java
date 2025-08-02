package org.shoppingdashboard.Service;

import org.shoppingdashboard.Entity.Customer;
import org.shoppingdashboard.Entity.CustomerCart;
import org.shoppingdashboard.Entity.Product;
import org.shoppingdashboard.Repository.CustomerCartRepository;
import org.shoppingdashboard.Repository.CustomerRepository;
import org.shoppingdashboard.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerCartRepository customerCartRepository;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private HttpSession session;

    public Product createProduct(Product product) {
//        if (product.getPrice() <= 0 || product.getStockQuantity() < 0) {
//            throw new IllegalArgumentException("Invalid product data");
//        }
        return productRepository.save(product);
    }

    public Page<Product> getProducts(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (updatedProduct.getPrice() <= 0 || updatedProduct.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Invalid product data");
        }
        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setStockQuantity(updatedProduct.getStockQuantity());
        product.setImageUrl(updatedProduct.getImageUrl());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private Map<String, List<Product>> userCart = new HashMap<>();


    public void addProductToCart(Long productId, Long customerId, int quantity) {
//    Long customerId;
        String username;
        if (session.getAttribute("impersonationMode") != null && (Boolean) session.getAttribute("impersonationMode")) {
            customerId = (Long) session.getAttribute("impersonatedCustomerId");
            username = (String) session.getAttribute("impersonatedCustomerUsername");
            System.out.println("Impersonated Customer ID: " + customerId);
            System.out.println("Impersonated Username: " + username);

        } else {
            username = (String) session.getAttribute("username");
            customerId = (Long) session.getAttribute("customerId");
        }


        if (username != null && customerId != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (product.getStockQuantity() >= quantity) {
                CustomerCart existingCartItem = customerCartRepository.findByCustomerIdAndProductId(customerId, productId);

                if (existingCartItem != null) {
                    // Update quantity if product already exists in the cart
                    existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
                    customerCartRepository.save(existingCartItem);
                } else {
                    // Add new product to the cart
                    Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
                    CustomerCart cart = new CustomerCart();
                    cart.setUsername(username);
                    cart.setProduct(product);
                    cart.setQuantity(quantity);
                    cart.setCustomer(customer);
                    cart.setProductName(product.getName());
                    customerCartRepository.save(cart);
                }

                // Update product stock quantity
                product.setStockQuantity(product.getStockQuantity() - quantity);
                productRepository.save(product);
            } else {
                throw new IllegalArgumentException("Insufficient stock for the product");
            }
        } else {
            throw new RuntimeException("User is not logged in");
        }
    }

    public void updateCartItem(Long productId, int quantity) {
        Long customerId;
        String username;
        if (session.getAttribute("impersonationMode") != null && (Boolean) session.getAttribute("impersonationMode")) {
            customerId = (Long) session.getAttribute("impersonatedCustomerId");
            username = (String) session.getAttribute("impersonatedCustomerUsername");
            System.out.println("Impersonated Customer ID: " + customerId);
            System.out.println("Impersonated Username: " + username);

        } else {
            username = (String) session.getAttribute("username");
            customerId = (Long) session.getAttribute("customerId");
        }

        if (username != null && customerId != null) {
            CustomerCart existingCartItem = customerCartRepository.findByCustomerIdAndProductId(customerId, productId);

            if (existingCartItem != null) {
                existingCartItem.setQuantity(quantity);
                customerCartRepository.save(existingCartItem);
            } else {
                throw new IllegalArgumentException("Cart item not found");
            }
        } else {
            throw new RuntimeException("User is not logged in");
        }
    }

    @Transactional
    public void removeProductFromCart(Long customerId, Long productId) {
        CustomerCart cartItem = customerCartRepository.findByCustomerIdAndProductId(customerId, productId);
        if (cartItem != null) {
            customerCartRepository.delete(cartItem);
        } else {
            throw new IllegalArgumentException("Cart item not found for the given customer and product.");
        }
    }

    public List<CustomerCart> getCartItems() {
        // Retrieve the username from the session
        String username;
        if (session.getAttribute("impersonationMode") != null && (Boolean) session.getAttribute("impersonationMode")) {
            username = (String) session.getAttribute("impersonatedCustomerUsername");
            System.out.println("Impersonated Username: " + username);

        } else {
            username = (String) session.getAttribute("username");
        }
        if (username != null) {
            return customerCartRepository.findByUsername(username);
        } else {
            throw new RuntimeException("User is not logged in");
        }
    }


    @Transactional
    public void clearCart(Long customerId) {
//    List<String> sanitizedProductNames = productNames.stream()
//            .map(name -> name.split(" \\(")[0]) // Extract name before "("
//            .collect(Collectors.toList());
        customerCartRepository.deleteByCustomerId(customerId);
    }

    public Product findByName(String name) {
        return productRepository.findByName(name);

    }

    public Product updateProductImage(Long productId, String imageUrl) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    product.setImageUrl(imageUrl);
    return productRepository.save(product);
}

}