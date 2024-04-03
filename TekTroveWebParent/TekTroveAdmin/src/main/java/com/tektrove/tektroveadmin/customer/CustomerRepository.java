package com.tektrove.tektroveadmin.customer;

import com.tektrovecommon.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE CONCAT(c.firstName, ' ', c.lastName, ' ', " +
            "c.email, ' ', c.addressLine1, ' ', c.addressLine2, ' ', c.city, ' ', " +
            "c.state, ' ', c.postalCode, ' ', c.country.name) LIKE %?1%")
    Page<Customer> findAll(String keyword, Pageable pageable);

    @Query("UPDATE Customer c SET c.enabled = ?2 WHERE c.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    Long countById(long id);

    Customer findByEmail(String email);
}
