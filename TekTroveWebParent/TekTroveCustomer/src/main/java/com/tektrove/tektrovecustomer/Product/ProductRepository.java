package com.tektrove.tektrovecustomer.Product;

import com.tektrovecommon.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.enabled = true AND " +
            "(p.category.id = :categoryId OR p.category.allParentIDs LIKE concat('%-', :categoryId, '-%'))")
    Page<Product> findByCategoryId(@Param("categoryId") Integer categoryId, Pageable pageable);

    Optional<Product> findByAlias(String productAlias);

    @Query(value = "SELECT * FROM products p WHERE MATCH(p.name, p.short_description, p.full_description) AGAINST(:keyword)", nativeQuery = true)
    Page<Product> searchProduct(String keyword, Pageable pageable);
}
