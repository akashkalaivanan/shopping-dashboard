package org.shoppingdashboard.Controller;

import org.shoppingdashboard.Entity.BrandingSettings;
import org.shoppingdashboard.Entity.Order;
import org.shoppingdashboard.Entity.Product;
import org.shoppingdashboard.Service.BrandingService;
import org.shoppingdashboard.Service.OrderService;
import org.shoppingdashboard.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class OrderController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BrandingService brandingService;

    @GetMapping("/customer/order/management")
    public String showOrderManagementPage(Model model, HttpSession session) {
        Long customerId = (Long) session.getAttribute("customerId");
        System.out.println("Customer ID from session: " + customerId);
        model.addAttribute("customerId", customerId);
        if (customerId != null) {
            List<Order> orders = orderService.getOrdersByCustomerId(customerId);
            model.addAttribute("orders", orders);
        } else {
            model.addAttribute("orders", null);
        }
        model.addAttribute("cartItems", productService.getCartItems());
        model.addAttribute("showShippingForm", false);
        model.addAttribute("orderSummary", null);
        model.addAttribute("orderPlaced", false);
        BrandingSettings settings = brandingService.getSettings();
        model.addAttribute("settings", settings);
        return "customer_order_management";
    }

    @PostMapping("/customer/order/cart/update/{id}")
    public String updateCartItem(@PathVariable Long id, @RequestParam int quantity) {
        productService.updateCartItem(id, quantity);
        return "redirect:/customer/order/management";
    }

    @PostMapping("/customer/order/cart/remove")
    public String removeCartItem(@RequestParam Long productId, HttpSession session) {
        Long customerId;
        if (session.getAttribute("impersonationMode") != null && (Boolean) session.getAttribute("impersonationMode")) {
            customerId = (Long) session.getAttribute("impersonatedCustomerId");
        } else {
            customerId = (Long) session.getAttribute("customerId");
        }

        productService.removeProductFromCart(customerId, productId);

        return "redirect:/customer/order/management";
    }

    @PostMapping("/customer/order/place")
    public String showShippingForm(Model model) {
        model.addAttribute("cartItems", productService.getCartItems());
        model.addAttribute("showShippingForm", true);
        model.addAttribute("orderSummary", null);
        model.addAttribute("orderPlaced", false);
        BrandingSettings settings = brandingService.getSettings();
        model.addAttribute("settings", settings);
        return "customer_order_management";
    }

    @PostMapping("/customer/order/summary")
    public String showOrderSummary(@RequestParam String houseNo,@RequestParam String streetName,@RequestParam String district,@RequestParam String state,@RequestParam String pinCode,@RequestParam String phone, Model model, HttpSession session) {

        String address = houseNo + ", " + streetName + ", " + district + ", " + state + ", " + pinCode + ", Phone: " + phone ;

        Long customerIdFromSession;
        if (session.getAttribute("impersonationMode") != null && (Boolean) session.getAttribute("impersonationMode")) {
            customerIdFromSession = (Long) session.getAttribute("impersonatedCustomerId");

        } else {
            customerIdFromSession = (Long) session.getAttribute("customerId");
        }
//        System.out.println("Customer ID passed to method: " + customerId);
        Order orderSummary = orderService.calculateOrderSummary(address, customerIdFromSession);

        model.addAttribute("cartItems", productService.getCartItems());
        model.addAttribute("showShippingForm", false);
        model.addAttribute("orderSummary", orderSummary);
        model.addAttribute("orderPlaced", false);
        BrandingSettings settings = brandingService.getSettings();
        model.addAttribute("settings", settings);
        return "customer_order_management";
    }

    @PostMapping("/customer/order/confirm")
    public String confirmOrder(@RequestParam String address, @RequestParam String productName, @RequestParam int quantity, Model model, HttpSession session) {
        String cleanedProductName = productName.split(" \\(")[0]; // Extract name before "("
        List<String> productNameList = List.of(cleanedProductName);
        System.out.println("Product Name List: " + productNameList);
        Long customerIdFromSession;
        if (session.getAttribute("impersonationMode") != null && (Boolean) session.getAttribute("impersonationMode")) {
            customerIdFromSession = (Long) session.getAttribute("impersonatedCustomerId");

        } else {
            customerIdFromSession = (Long) session.getAttribute("customerId");
        }
        orderService.placeOrder(address, customerIdFromSession, productName, quantity);
        productService.clearCart(customerIdFromSession);
        List<Order> updatedOrders = orderService.getOrdersByCustomerId(customerIdFromSession);
        model.addAttribute("orders", updatedOrders);
        model.addAttribute("cartItems", productService.getCartItems());
        model.addAttribute("showShippingForm", false);
        model.addAttribute("orderSummary", null);
        model.addAttribute("orderPlaced", true);
        BrandingSettings settings = brandingService.getSettings();
        model.addAttribute("settings", settings);
        return "customer_order_management";
    }

    @GetMapping("/admin/order-management")
    public String showAdminOrderManagementPage(Model model) {
        List<Order> orders = orderService.getAllOrdersWithProducts();
        model.addAttribute("orders", orders);
        return "admin_order_management";
    }

    @PostMapping("/admin/order/update/{id}")
    public ResponseEntity<String> updateOrderDetails(@PathVariable Long id,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(required = false) String productNames,
                                                     @RequestParam(required = false) String productQuantities,
                                                     @RequestParam(required = false) String address,
                                                     @RequestParam(required = false) Double totalAmount) {
        List<String> productNameList = productNames != null ? Arrays.asList(productNames.split(",")) : null;
        List<Integer> productQuantityList = productQuantities != null ? Arrays.stream(productQuantities.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList()) : null;

        if (productNameList != null && productQuantityList != null) {
            for (int i = 0; i < productNameList.size(); i++) {
                String productName = productNameList.get(i);
                int quantity = productQuantityList.get(i);

                Product product = productService.findByName(productName);
                if (product == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Product not found: " + productName);
                }

                if (quantity > product.getStockQuantity()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Insufficient stock for product: " + productName + ". Available stock: " + product.getStockQuantity());
                }
            }
        }

        orderService.updateOrderDetails(id, status, productNameList, productQuantityList, address, totalAmount);
        return ResponseEntity.ok("Order updated successfully");
    }

    @DeleteMapping("/admin/order/remove-product")
    public ResponseEntity<String> removeProductFromOrder(@RequestParam Long orderId, @RequestParam String productName) {
        orderService.removeProductFromOrder(orderId, productName);
        Double updatedTotalAmount = orderService.getOrderTotalAmount(orderId);
        return ResponseEntity.ok(String.valueOf(updatedTotalAmount));
    }


    @DeleteMapping("/admin/order/delete/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id); // Deletes the order and associated products
            return ResponseEntity.ok("Order deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete order");
        }
    }

    @PostMapping("/admin/product/check-order-products/{id}")
    @ResponseBody
    public boolean checkProductInOrderProducts(@PathVariable Long id) {
        return orderService.isProductInOrderProducts(id);
    }

    @GetMapping("/admin/order/aggregate/{groupBy}")
    @ResponseBody
    public Map<String, Double> getAggregatedData(@PathVariable String groupBy) {
        if ("customer".equalsIgnoreCase(groupBy)) {
            return orderService.aggregateTotalAmountByCustomer();
        } else if ("product".equalsIgnoreCase(groupBy)) {
            return orderService.aggregateTotalAmountByProduct();
        } else {
            throw new IllegalArgumentException("Invalid grouping criteria: " + groupBy);
        }


    }

@GetMapping("/admin/stats")
@ResponseBody
public Map<String, Object> getStats() {
    return orderService.getStats();
}

}