package org.shoppingdashboard.Controller;

import org.shoppingdashboard.Entity.Admin;
import org.shoppingdashboard.Entity.BrandingSettings;
import org.shoppingdashboard.Entity.Customer;
import org.shoppingdashboard.Service.AuthService;
import org.shoppingdashboard.Service.BrandingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
//@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private BrandingService brandingService;

    @GetMapping("/")
    public String showHomePage(@RequestParam(required = false) String formType, Model model) {
    model.addAttribute("formType", formType != null ? formType : "registration"); // Default to registration
    return "home"; // Serve the home page
    }


@PostMapping("/register")
public String registerCustomer(
        @RequestParam String firstname,
        @RequestParam String lastname,
        @RequestParam String username,
        @RequestParam String email,
        @RequestParam String password,
        Model model) {


    if (authService.isEmailAlreadyRegistered(email)) {
        model.addAttribute("message", "Email is already registered.");
        model.addAttribute("messageType", "error");
        System.out.println("Email is already registered: " + model);
        return "home"; // Return the view name
    }

    if (!authService.isPasswordStrong(password)) {
//        response.put("error", "Password is not strong enough.");
        model.addAttribute("message", "Password is not strong enough.");
        model.addAttribute("messageType", "error");
        return "home";
    }

    try {
        Customer customer = new Customer(firstname,lastname,username, email, password);
        authService.registerCustomer(customer);
        model.addAttribute("message", "Registration successful.");
        model.addAttribute("messageType", "success");
        return "home";

    } catch (Exception e) {
        model.addAttribute("message", "Registration failed. Please try again.");
        model.addAttribute("messageType", "error");
        return "home";
    }

//    return response;
}

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,HttpSession httpSession, Model model) {
        try {
             Admin admin = authService.authenticateAdmin(username, password);
            if (admin != null) {
                model.addAttribute("message", "Welcome Admin");
                return "admin_dashboard";
            }

            Customer customer = authService.authenticateCustomer(username, password);
            if (customer != null) {
                if(customer.getActive()==false)
                {
                    System.out.println("000here");
                    model.addAttribute("message", "You don't have access.");
                    return "home";
                }
                httpSession.setAttribute("username", customer.getUsername());
                httpSession.setAttribute("customerId", customer.getId());
                model.addAttribute("message", "Welcome");
                BrandingSettings settings = brandingService.getSettings();
                model.addAttribute("settings", settings);
                return "customer_dashboard";
            }

            model.addAttribute("message", "Invalid credentials");
            return "home";
        } catch (Exception e) {
            model.addAttribute("message", "Login failed. Please try again.");
            return "error";
        }
    }

    @GetMapping("/api/user-details")
    public Map<String, Object> getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> userDetailsMap = new HashMap<>();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            userDetailsMap.put("username", userDetails.getUsername());
            userDetailsMap.put("roles", userDetails.getAuthorities());
        } else {
            userDetailsMap.put("username", "Guest");
            userDetailsMap.put("roles", "None");
        }
        System.out.println("User Details: " + userDetailsMap);
        return userDetailsMap;
    }

    @GetMapping("/login")
public String showLoginPage(HttpSession session, Model model) {
    // Debugging session attributes
    System.out.println("Session Attributes:");
    System.out.println("impersonationMode: " + session.getAttribute("impersonationMode"));
    System.out.println("impersonatedCustomerId: " + session.getAttribute("impersonatedCustomerId"));
    System.out.println("impersonatedCustomerUsername: " + session.getAttribute("impersonatedCustomerUsername"));

    // Check for impersonation mode
    if (Boolean.TRUE.equals(session.getAttribute("impersonationMode"))) {
        Long impersonatedCustomerId = (Long) session.getAttribute("impersonatedCustomerId");
        String impersonatedCustomerUsername = (String) session.getAttribute("impersonatedCustomerUsername");

        if (impersonatedCustomerId != null && impersonatedCustomerUsername != null) {
            session.setAttribute("username", impersonatedCustomerUsername);
            session.setAttribute("customerId", impersonatedCustomerId);
            System.out.println("Redirecting to customer dashboard as impersonated user.");
            BrandingSettings settings = brandingService.getSettings();
            model.addAttribute("settings", settings);
            return "redirect:/customer_dashboard"; // Redirect to customer dashboard
        } else {
            System.out.println("Impersonation attributes are null. Redirecting to login page.");
        }
    }

    // Check for regular login
    if (session.getAttribute("username") != null) {
        if (session.getAttribute("customerId") != null) {
            System.out.println("Redirecting to customer dashboard.");
            BrandingSettings settings = brandingService.getSettings();
            model.addAttribute("settings", settings);
            return "redirect:/customer_dashboard"; // Redirect customer to their dashboard
        } else {
            System.out.println("Redirecting to admin dashboard.");
            return "redirect:/admin/dashboard"; // Redirect admin to admin dashboard
        }
    }

    // Default case: show login page
    model.addAttribute("message", "Please log in to continue.");
    System.out.println("Showing login page.");
    return "home"; // Serve the login page
}

@GetMapping("/logout")
public String logout(HttpSession session, Model model) {
    session.invalidate(); // Invalidate the session to log out
    model.addAttribute("message", "You have been logged out. Please log in again.");
    return "redirect:/login"; // Redirect to the login page with the message
}

//@PostMapping("/forgot-password")
//public String forgotPassword(@RequestParam("email") String email, Model model) {
//    try {
//        // Check if the email exists in the database
//        Customer customer = authService.findCustomerByEmail(email);
//        System.out.println("Customer found: " + customer);
//        if (customer != null) {
//            // Generate a reset token
//            String resetToken = authService.generateResetToken();
//            authService.saveResetToken(customer.getId(), resetToken);
//
//            // Send reset password email
//            authService.sendResetPasswordEmail(email, resetToken);
//
//            model.addAttribute("message", "Password reset link has been sent to your email.");
//            model.addAttribute("messageType", "success");
//        } else {
//            model.addAttribute("message", "Email not found.");
//            model.addAttribute("messageType", "error");
//        }
//    } catch (Exception e) {
//        model.addAttribute("message", "An error occurred while processing your request.");
//        model.addAttribute("messageType", "error");
//    }
//    return "home"; // Redirect to the home page with the message
//}
}