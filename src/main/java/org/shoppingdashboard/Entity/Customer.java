package org.shoppingdashboard.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="firstname")
    private String firstname;

    @Column(name="lastname")
    private String lastname;
    @Column(name="username")
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="resetToken")
    private String resetToken;
    @Column(name="email")
    private String email;


    @Column(name="active", nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    private List<CustomerCart> customerCarts;

    public Customer() {}

    // Constructor with arguments
    public Customer(String firstname,String lastname,String username, String email, String password) {
        this.firstname = firstname;
        this.lastname=lastname;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
