package org.shoppingdashboard.Controller;

import org.shoppingdashboard.Entity.BrandingSettings;
import org.shoppingdashboard.Entity.Customer;
import org.shoppingdashboard.Entity.Order;
import org.shoppingdashboard.Service.AdminCustomerService;
import org.shoppingdashboard.Service.BrandingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class AdminCustomerController {

    @Autowired
    private AdminCustomerService adminCustomerService;

    @Autowired
    private BrandingService brandingService;

 @GetMapping("/admin/customer/dashboard")
public String showCustomerDashboard(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "active", required = false) Boolean active,
        Model model) {
    List<Customer> customers = adminCustomerService.getFilteredCustomers(search, active);
    model.addAttribute("customers", customers);
    return "admin_customer_dashboard";
}

    @PostMapping("/admin/customer/create")
public String createCustomer(@RequestParam String firstname,@RequestParam String lastname,@RequestParam String userName,
                             @RequestParam String password,
                              @RequestParam String email,
                              @RequestParam(required =false) Boolean active)
    {
        adminCustomerService.createCustomer(firstname,lastname,userName,password, email, active);
        return "redirect:/admin/customer/dashboard";
    }

    @GetMapping("/admin/customer/{id}/profile")
    public String viewCustomerProfile(@PathVariable Long id, Model model) {
        Customer customer = adminCustomerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        model.addAttribute("orderHistory", adminCustomerService.getOrderHistoryByCustomerId(id));
        return "admin_customer_dashboard";
    }

    @PostMapping("/admin/customer/update")
    public String updateCustomerProfile(@RequestParam Long customerId,
                                        @RequestParam String firstname,
                                        @RequestParam String lastname,
                                        @RequestParam String userName,
                                        @RequestParam String email,
                                        @RequestParam Boolean active) {
        System.out.println("Updati0000 " + active);
        System.out.println("999"+lastname);
        adminCustomerService.updateCustomerProfile(customerId,firstname,lastname, userName, email, active);
        return "redirect:/admin/customer/dashboard";
    }

    @GetMapping("/admin/customer/{id}/orders")
    @ResponseBody
    public List<Order> getOrderHistory(@PathVariable Long id) {
        return adminCustomerService.getOrderHistoryByCustomerId(id);
    }

//    @PostMapping("/admin/customer/{id}/reset-password")
//    public String resetCustomerPassword(@PathVariable Long id, Model model) {
//        String tempPassword = adminCustomerService.resetPassword(id);
//        model.addAttribute("tempPassword", tempPassword);
//        return "admin_customer_dashboard";
//    }

    @PostMapping("/admin/customer/{id}/delete")
    public String deleteCustomer(@PathVariable Long id) {
        adminCustomerService.deleteCustomer(id);
        return "redirect:/admin/customer/dashboard";
    }

@PostMapping("/admin/customer/{customerId}/impersonate")
public String impersonateCustomer(@PathVariable Long customerId, HttpSession session,Model model) {
        System.out.println("Impersonating customer with ID: " + customerId);
    adminCustomerService.impersonateCustomer(customerId, session);
    BrandingSettings settings = brandingService.getSettings();
    model.addAttribute("settings", settings);
    return "customer_dashboard"; // Redirect directly to customer dashboard
}

  @PostMapping("/admin/customer/exit-impersonation")
public String exitImpersonation(HttpSession session) {
   adminCustomerService.exitImpersonation(session);
    return "admin_dashboard";
}

@GetMapping("/profile")
public String showProfileManagementPage(HttpSession session, Model model) {
       Long customerId;
        System.out.println("Entering profile management page"+ session.getAttribute("customerId"));
    if (session.getAttribute("impersonationMode") != null && (Boolean) session.getAttribute("impersonationMode")) {
        customerId = (Long) session.getAttribute("impersonatedCustomerId");

    } else {
        customerId = (Long) session.getAttribute("customerId");
    }
    if (customerId == null) {
        throw new RuntimeException("User is not logged in");
    }

    // Fetch the customer details using the service layer
    Customer customer = adminCustomerService.getCustomerById(customerId);
    System.out.println("Customer ID from session in view: " + customerId);
    model.addAttribute("customer", customer);
    BrandingSettings settings = brandingService.getSettings();
    model.addAttribute("settings", settings);
    // Return the view name for the profile management page
    return "customer_profile_management";
}

@PostMapping("/profile/update")
public String updateProfileManagementPage(
        @RequestParam String firstname,
        @RequestParam String lastname,
        @RequestParam String username,
        @RequestParam String email,
        HttpSession session,Model model) {
        Long customerId;
    if (session.getAttribute("impersonationMode") != null && (Boolean) session.getAttribute("impersonationMode")) {
        customerId= (Long) session.getAttribute("impersonatedCustomerId");

    } else {
        customerId = (Long) session.getAttribute("customerId");
    }
    if (customerId == null) {
        throw new RuntimeException("User is not logged in");
    }
    adminCustomerService.updateCustomerProfile(customerId,firstname,lastname, username, email, true);
    Customer updatedCustomer = adminCustomerService.getCustomerById(customerId);
    model.addAttribute("customer", updatedCustomer);
    model.addAttribute("successMessage", "Profile updated successfully.");
    BrandingSettings settings = brandingService.getSettings();
    model.addAttribute("settings", settings);
    return "customer_profile_management";
}

@PostMapping("/profile/reset-password")
public String resetPassword(
        @RequestParam String currentPassword,
        @RequestParam String newPassword,
        HttpSession session,Model model) {
   Long customerId;
    if (session.getAttribute("impersonationMode") != null && (Boolean) session.getAttribute("impersonationMode")) {
        customerId= (Long) session.getAttribute("impersonatedCustomerId");

    } else {
        customerId = (Long) session.getAttribute("customerId");
    }    if (customerId == null) {
        throw new RuntimeException("User is not logged in");
    }

    // Validate current password
    Customer customer = adminCustomerService.getCustomerById(customerId);
    if (!adminCustomerService.validatePassword(customer, currentPassword)) {
        throw new RuntimeException("Current password is incorrect");
    }

    // Update password
    adminCustomerService.updatePassword(customerId, newPassword);
    model.addAttribute("customer", customer);
    model.addAttribute("successMessageReset", "Password updated successfully.");
    BrandingSettings settings = brandingService.getSettings();
    model.addAttribute("settings", settings);
    return "customer_profile_management";
}

    @PostMapping("/admin/customer/{id}/reset-password")
    @ResponseBody
    public Map<String, String> resetPassword(@PathVariable Long id) {
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        adminCustomerService.updateCustomerPassword(id, tempPassword);
        Map<String, String> response = new HashMap<>();
        response.put("customerId", String.valueOf(id));
        response.put("tempPassword", tempPassword);
        return response;
    }
    }