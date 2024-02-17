package com.tektrove.tektroveadmin.brand;

import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    @Query("SELECT b FROM Brand b where b.name LIKE %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);
}
