package org.shoppingdashboard.Repository;

import org.shoppingdashboard.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer")
    List<Order> findAllWithCustomer();


// File: `src/main/java/org/shoppingdashboard/Repository/OrderRepository.java`
@Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
long countByStatus(String status);}