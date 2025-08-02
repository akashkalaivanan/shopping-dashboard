package org.shoppingdashboard.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false)
    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

//    @Column(nullable = false)
    private String status;

//    @Column(nullable = false)
    private double totalAmount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String productNames;

    @Column(nullable = false)
    private int productQuantities;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts= new ArrayList<>();


public void addOrderProduct(OrderProduct orderProduct) {
    orderProducts.add(orderProduct);
    orderProduct.setOrder(this);
}

public void removeOrderProduct(OrderProduct orderProduct) {
    orderProducts.remove(orderProduct);
    orderProduct.setOrder(null);
}

public void setOrderProducts(List<OrderProduct> orderProducts) {
    this.orderProducts.clear();
    if (orderProducts != null) {
        for (OrderProduct orderProduct : orderProducts) {
            addOrderProduct(orderProduct);
        }
    }
}    }
