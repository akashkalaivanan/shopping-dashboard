package org.shoppingdashboard.Repository;

import org.shoppingdashboard.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Product findByName(String name);

//    Optional  findByName(String name);
}