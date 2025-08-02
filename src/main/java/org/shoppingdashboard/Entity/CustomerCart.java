package org.shoppingdashboard.Entity;

import lombok.Getter;
import lombok.Setter;
import org.shoppingdashboard.Entity.Product;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "customercart")
public class CustomerCart{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    @Column(name = "product_name", nullable = false)
    private String productName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

}