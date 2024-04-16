package com.tektrove.tektrovecustomer.cart;

import com.tektrovecommon.entity.CartItem;
import com.tektrovecommon.entity.Customer;
import com.tektrovecommon.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCustomer(Customer customer);
    Optional<CartItem> findByCustomer_IdAndProduct_Id(Integer customer, Integer product);
    void deleteByCustomerAndProduct(Customer customer, Product product);
    @Modifying
    @Query("update CartItem c set c.quantity = ?1 where c.customer.id = ?2 and c.product.id = ?3")
    void updateQuantity(Integer quantity, Integer customerId, Integer productId);

    void deleteByCustomer_IdAndProduct_Id(Integer customerId, Integer productId);
}
