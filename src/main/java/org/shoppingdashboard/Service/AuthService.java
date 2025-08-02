package org.shoppingdashboard.Service;

import org.shoppingdashboard.Entity.Admin;
import org.shoppingdashboard.Entity.Customer;
import org.shoppingdashboard.Repository.AdminRepository;
import org.shoppingdashboard.Repository.CustomerRepository;
import org.shoppingdashboard.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private EmailService emailService;

    public String registerCustomer(Customer customer) {
        customer.setActive(true); // Set customer as active by default
        String pass=passwordEncoder.encode(customer.getPassword());
        customer.setPassword(pass);
        System.out.println("Encoded Password: " + pass);
        customerRepository.save(customer);
        return jwtUtil.generateToken(customer.getUsername());
    }

    public Admin authenticateAdmin(String username, String password) {
        Optional<Admin> admin = adminRepository.findByUsernameAndPassword(username, password);
        return admin.orElse(null); // Return Admin object if valid, otherwise null
    }

 public Customer authenticateCustomer(String username, String password) {
    Optional<Customer> customerOptional = customerRepository.findByUsername(username);
    if (customerOptional.isPresent()) {
        Customer customer = customerOptional.get();
        if (passwordEncoder.matches(password, customer.getPassword())) {
            return customer; // Return Customer object if password matches
        }
    }
    return null; // Return null if customer does not exist or password does not match
}

    public String generateAdminToken(Admin admin) {
        return jwtUtil.generateToken(admin.getUsername());
    }

    public String generateCustomerToken(Customer customer) {
        return jwtUtil.generateToken(customer.getUsername());
    }

    public boolean isPasswordStrong(String password) {
        // Password strength validation: Minimum 8 characters, at least one uppercase, one lowercase, one digit, and one special character
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordPattern);
    }

    public boolean isEmailAlreadyRegistered(String email) {
        System.out.println("Checking if email is already registered: " + customerRepository.findByEmail(email));
        Customer user = customerRepository.findByEmail(email).orElse(null);
        if (user != null) {
            System.out.println("Email is already registered: " + email);
            return true; // Email is already registered
        }
        else {
            System.out.println("Email is not registered: " + email);  // Email is not registered
        return false; }
}

//    public Customer findCustomerByEmail(String email) {
//        return customerRepository.findByEmail(email).orElse(null);
//    }
//
//    public String generateResetToken() {
//        return UUID.randomUUID().toString();
//    }
//
//    public void saveResetToken(Long customerId, String resetToken) {
//        Optional<Customer> customerOptional = customerRepository.findById(customerId);
//        if (customerOptional.isPresent()) {
//            Customer customer = customerOptional.get();
//            customer.setResetToken(resetToken);
//            customerRepository.save(customer);
//        }
//    }
//
//    public void sendResetPasswordEmail(String email, String resetToken) {
//        System.out.println("0000here");
//        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
//        String subject = "Password Reset Request";
//        String message = "Click the link below to reset your password:\n" + resetLink;
//        emailService.sendEmail(email, subject, message);
//    }

    }