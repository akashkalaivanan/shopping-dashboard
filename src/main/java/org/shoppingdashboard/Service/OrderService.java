package org.shoppingdashboard.Service;

import org.shoppingdashboard.Entity.*;
import org.shoppingdashboard.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerCartRepository customerCartRepository;

    @Transactional
public void placeOrder(String address, Long customerId, String productName, int quantity) {
    // Retrieve customer details
    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    // Retrieve cart items for the customer
    List<CustomerCart> cartItems = customerCartRepository.findByCustomerId(customerId);

    if (cartItems.isEmpty()) {
        throw new IllegalArgumentException("Cart is empty");
    }

    // Calculate total amount
    double totalAmount = cartItems.stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();

    // Concatenate product names
    String productNames = cartItems.stream()
            .map(item -> item.getProduct().getName())
            .reduce((name1, name2) -> name1 + ", " + name2)
            .orElse("");

    // Create and populate the Order object
    Order order = new Order();
    order.setAddress(address);
    order.setCustomer(customer);
    order.setOrderDate(new java.util.Date());
    order.setStatus("Placed");
    order.setTotalAmount(totalAmount);
    order.setProductNames(productNames);
    order.setProductQuantities(quantity); // Assuming quantity is the total quantity of products in the order

        // Save the order

    orderRepository.save(order);

        List<OrderProduct> orderProducts = new ArrayList<>();
        for (CustomerCart cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProductName(cartItem.getProduct().getName());
            orderProduct.setQuantity(cartItem.getQuantity()); // Set correct quantity
            orderProduct.setProduct(product);
            orderProductRepository.save(orderProduct);
            orderProducts.add(orderProduct);
        }
        order.setOrderProducts(orderProducts);
        orderRepository.save(order);


}



@Transactional
public List<Order> getAllOrdersWithProducts() {
    List<Order> orders = orderRepository.findAllWithCustomer(); // Fetch orders with customer details
    for (Order order : orders) {
        List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(order.getId()); // Fetch associated OrderProduct details
        order.setOrderProducts(orderProducts); // Set OrderProduct details in the Order object
    }
    return orders; // Return the list of orders with associated OrderProduct details
}

@Transactional
public Order calculateOrderSummary(String address, Long customerId) {
    // Validate customer ID
    if (customerId == null) {
        throw new IllegalArgumentException("Customer ID cannot be null");
    }

    // Retrieve cart items for the customer
    List<CustomerCart> cartItems = customerCartRepository.findByCustomerId(customerId);

    if (cartItems == null || cartItems.isEmpty()) {
        throw new IllegalArgumentException("Cart is empty");
    }

    // Calculate total amount
    double totalAmount = cartItems.stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();

    // Concatenate product details
    String productDetails = cartItems.stream()
            .map(item -> item.getProduct().getName() + " (x" + item.getQuantity() + ") - $" + item.getProduct().getPrice())
            .reduce((detail1, detail2) -> detail1 + "\n" + detail2)
            .orElse("");

    String productQuantities = cartItems.stream()
            .map(item -> item.getQuantity() + "")
            .reduce((qty1, qty2) -> qty1 + ", " + qty2)
            .orElse("");


    // Create and populate the Order object
    Order orderSummary = new Order();
    orderSummary.setAddress(address);
    orderSummary.setTotalAmount(totalAmount);
    orderSummary.setProductNames(productDetails);

    return orderSummary;
}

public List<Order> getOrdersByCustomerId(Long customerId) {
    if (customerId == null) {
        throw new IllegalArgumentException("Customer ID cannot be null");
    }
    return orderRepository.findByCustomerId(customerId);
}

public List<Order> getAllOrders() {
    return orderRepository.findAllWithCustomer();
}

@Transactional
public void updateOrderDetails(Long orderId, String status, List<String> productNames, List<Integer> productQuantities, String address, Double totalAmount) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    if (status != null) {
        order.setStatus(status);
    }
    if (address != null) {
        order.setAddress(address);
    }

    if (productNames != null && productQuantities != null && productNames.size() == productQuantities.size()) {
        List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);
        for (int i = 0; i < productNames.size(); i++) {
            String productName = productNames.get(i);
            int quantity = productQuantities.get(i);

           Product product = productRepository.findByName(productName);
if (product == null) {
    throw new IllegalArgumentException("Product not found: " + productName);
}
            OrderProduct orderProduct = orderProducts.stream()
                    .filter(op -> op.getProductName().equals(productName))
                    .findFirst()
                    .orElse(new OrderProduct());

            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setProductName(productName);
            orderProduct.setQuantity(quantity);
            orderProductRepository.save(orderProduct);
        }
    }

    if(totalAmount!= null) {
        order.setTotalAmount(totalAmount);
    }
    orderRepository.save(order);
}

@Transactional
public void removeProductFromOrder(Long orderId, String productName) {
    Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));

    // Remove the product from OrderProduct table
    orderProductRepository.deleteByOrderIdAndProductName(orderId, productName);

    // Fetch remaining OrderProducts
    List<OrderProduct> remainingOrderProducts = orderProductRepository.findByOrderId(orderId);

    // Recalculate the total amount
    double updatedTotalAmount = remainingOrderProducts.stream()
            .mapToDouble(orderProduct -> {
                Product product = productRepository.findByName(orderProduct.getProductName());
                if (product == null) {
                    throw new IllegalArgumentException("Product not found");
                }
                return product.getPrice() * orderProduct.getQuantity();
            })
            .sum();

    // Update the product names and total amount in the Order entity
    String updatedProductNames = remainingOrderProducts.stream()
            .map(OrderProduct::getProductName)
            .collect(Collectors.joining(","));

    order.setProductNames(updatedProductNames);
    order.setTotalAmount(updatedTotalAmount);
    orderRepository.save(order);
}

@Transactional
public Double getOrderTotalAmount(Long orderId) {
    // Fetch all OrderProduct entities for the given orderId
    List<OrderProduct> orderProducts = orderProductRepository.findByOrderId(orderId);

    // Calculate the total amount
    return orderProducts.stream()
            .mapToDouble(orderProduct -> {
                Product product = orderProduct.getProduct();
                if (product == null) {
                    throw new IllegalArgumentException("Product not found for OrderProduct ID: " + orderProduct.getId());
                }
                return product.getPrice() * orderProduct.getQuantity();
            })
            .sum();
}
@Transactional
public void deleteOrder(Long orderId) {
    // Delete associated OrderProduct entries
    orderProductRepository.deleteByOrderId(orderId);

    // Delete the Order entry
    orderRepository.deleteById(orderId);
}

public boolean isProductInOrderProducts(Long productId) {
    return orderProductRepository.existsByProductId(productId);
}

    public Map<String, Double> aggregateTotalAmountByCustomer() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCustomer().getUsername(),
                        Collectors.summingDouble(Order::getTotalAmount)
                ));
    }

    public Map<String, Double> aggregateTotalAmountByProduct() {
        List<Order> orders = orderRepository.findAll();

        Map<String, Double> productTotals = new HashMap<>();

        for (Order order : orders) {
            for (OrderProduct orderProduct : order.getOrderProducts()) {
                String productName = orderProduct.getProduct().getName();
                double productTotal = orderProduct.getQuantity() * orderProduct.getProduct().getPrice();

                productTotals.merge(productName, productTotal, Double::sum);
            }
        }

        return productTotals;
    }

public Map<String, Object> getStats() {
    Map<String, Object> stats = new HashMap<>();
    System.out.println("Total Products: " + productRepository.count());
    System.out.println("Total Customers: " + customerRepository.count());
    System.out.println("Orders Placed: " + orderRepository.countByStatus("Placed"));
    System.out.println("Orders Shipped: " + orderRepository.countByStatus("Shipped"));
    stats.put("totalProducts", productRepository.count());
    stats.put("totalCustomers", customerRepository.count());
    stats.put("ordersPlaced", orderRepository.countByStatus("Placed"));
    stats.put("ordersShipped", orderRepository.countByStatus("Shipped"));
    return stats;
}

}