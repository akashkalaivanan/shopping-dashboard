package org.shoppingdashboard.Repository;

import org.shoppingdashboard.Entity.Order;
import org.shoppingdashboard.Entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {


    List<OrderProduct> findByOrderId(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderProduct op WHERE op.order.id = :orderId AND op.productName = :productName")
    void deleteByOrderIdAndProductName(@Param("orderId") Long orderId, @Param("productName") String productName);

    void deleteByOrderId(Long orderId);

    boolean existsByProductId(Long productId);
}