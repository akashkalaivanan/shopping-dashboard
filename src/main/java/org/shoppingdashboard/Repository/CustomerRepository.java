package org.shoppingdashboard.Repository;

import org.shoppingdashboard.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsernameAndPassword(String username, String password);

    Optional<Customer> findByUsername(String username);

    Optional<Customer> findByEmail(String email);


    @Query("SELECT c FROM Customer c WHERE (c.username LIKE %:search% OR c.email LIKE %:search%) AND c.active = :active")
    List<Customer> findBySearchAndActive(@Param("search") String search, @Param("active") Boolean active);

    @Query("SELECT c FROM Customer c WHERE c.username LIKE %:search% OR c.email LIKE %:search%")
    List<Customer> findBySearch(@Param("search") String search);

    List<Customer> findByActive(Boolean active);

}
