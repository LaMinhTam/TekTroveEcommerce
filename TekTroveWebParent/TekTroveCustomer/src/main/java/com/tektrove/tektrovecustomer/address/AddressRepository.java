package com.tektrove.tektrovecustomer.address;

import com.tektrovecommon.entity.customer.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByCustomer_Id(Integer customerId);

    Address findByCustomer_IdAndId(Integer customerId, Integer addressId);

    @Query("DELETE FROM Address a WHERE a.customer.id = :customerId AND a.id = :addressId")
    @Modifying
    void DeleteByCustomer_IdAndId(Integer customerId, Integer addressId);

    @Transactional
    @Modifying
    @Query("UPDATE Address a SET a.isDefaultAddress = CASE " +
            "WHEN a.id = :addressId THEN true " +
            "ELSE false " +
            "END " +
            "WHERE a.customer.id = :customerId")
    void updateDefaultAddress(Integer customerId, Integer addressId);
}
