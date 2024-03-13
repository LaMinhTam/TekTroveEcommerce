package com.tektrove.tektroveadmin.product;

import com.tektrovecommon.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%"
            + " OR p.shortDescription LIKE %?1%"
            + " OR p.fullDescription LIKE %?1%"
            + " OR p.brand.name LIKE %?1%"
            + " OR p.category.name LIKE %?1%")
    Page<Product> findAll(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE "
            + "concat(p.category.allParentIDs, p.category.id, '-') LIKE %?1%"
            + " AND (p.name LIKE %?2%"
            + " OR p.shortDescription LIKE %?2%"
            + " OR p.fullDescription LIKE %?2%"
            + " OR p.brand.name LIKE %?2%)")
    Page<Product> findByCategoryIdAndKeyword(String categoryId, String keyword,Pageable pageable);

    Product findByName(String name);

    @Modifying
    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    void updateEnabledStatus(Integer id, boolean enabled);

    int countById(Integer id);
}
