package com.tektrove.tektrovecustomer.customer;

import com.tektrovecommon.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByActivationCode(String activationCode);

    Optional<Customer> findByIdAndEnabledTrue(Integer id);

    @Modifying
    @Query("UPDATE Customer c SET c.enabled = TRUE, c.activationCode = NULL WHERE c.id = :id")
    public void enabledCustomer(Integer id);
}
