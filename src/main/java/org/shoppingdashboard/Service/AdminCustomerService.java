package org.shoppingdashboard.Service;

import org.shoppingdashboard.Entity.Customer;
import org.shoppingdashboard.Entity.Order;
import org.shoppingdashboard.Repository.CustomerCartRepository;
import org.shoppingdashboard.Repository.CustomerRepository;
import org.shoppingdashboard.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class AdminCustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerCartRepository customerCartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<Customer> searchCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public List<Order> getOrderHistoryByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public void createCustomer(String firstName,String lastName,String userName,String password, String email, Boolean active) {
    Customer customer = new Customer();
    customer.setFirstname(firstName);
    customer.setLastname(lastName);
    customer.setUsername(userName);
    String pass=passwordEncoder.encode(password); // Hash the password
    System.out.println("0000"+pass);
        customer.setPassword(pass);
    customer.setEmail(email);
    customer.setActive(active);
    customerRepository.save(customer);
}

    public void updateCustomerProfile(Long id,String firstName,String lastName, String userName, String email, Boolean active) {
        Customer customer = getCustomerById(id);
        customer.setFirstname(firstName);
        customer.setLastname(lastName);
        customer.setUsername(userName);
        customer.setEmail(email);
        customer.setActive(active);
        customerRepository.save(customer);
    }


    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customerCartRepository.deleteByCustomerId(customer.getId()); // Delete associated cart
        customerRepository.deleteById(customer.getId()); // Delete customer record
    }


public void impersonateCustomer(Long customerId, HttpSession session) {
    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
     System.out.println("0000"+customerId);
     System.out.println("Impersonating customer: " + customer.getUsername());
    session.setAttribute("impersonationMode", true);
    session.setAttribute("impersonatedCustomerId", customerId);
    session.setAttribute("impersonatedCustomerUsername", customer.getUsername());
}
    public void exitImpersonation(HttpSession session) {
        session.removeAttribute("impersonationMode");
        session.removeAttribute("impersonatedCustomerId");
        session.removeAttribute("impersonatedCustomerUsername");
        session.removeAttribute("impersonatedCustomerEmail");
    }

    public boolean validatePassword(Customer customer, String currentPassword) {
        // Compare the hashed currentPassword with the stored password
        System.out.println("0000"+customer.getPassword());
        System.out.println("1111"+currentPassword);
        return passwordEncoder.matches(currentPassword, customer.getPassword());
    }

    public void updatePassword(Long customerId, String newPassword) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setPassword(passwordEncoder.encode(newPassword)); // Hash the new password
        customerRepository.save(customer);
    }

    @Transactional
    public void updateCustomerPassword(Long customerId, String tempPassword) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        String pass=passwordEncoder.encode(tempPassword);
        customer.setPassword(pass); // Assuming password is stored in plain text (consider hashing for security)
        customerRepository.save(customer);
    }

    public List<Customer> getFilteredCustomers(String search, Boolean active) {
        if (search != null && active != null) {
            return customerRepository.findBySearchAndActive(search, active);
        } else if (search != null) {
            return customerRepository.findBySearch(search);
        } else if (active != null) {
            return customerRepository.findByActive(active);
        } else {
            return customerRepository.findAll();
        }
    }
}