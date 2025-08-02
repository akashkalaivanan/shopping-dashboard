package org.shoppingdashboard.Repository;

import org.shoppingdashboard.Entity.CustomerCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerCartRepository extends JpaRepository<CustomerCart, Long> {
    List<CustomerCart> findByUsername(String username);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM customercart c WHERE customer_id = :customerId AND product_name IN (:productNames)",nativeQuery = true)
    void deleteByCustomerIdAndProductName(Long customerId, List<String> productNames);

    void deleteByCustomerId(Long customerId);


    CustomerCart findByCustomerIdAndProductId(Long customerId, Long productId);

    List<CustomerCart> findByCustomerId(Long customerId);
}
