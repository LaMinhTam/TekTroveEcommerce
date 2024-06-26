package com.tektrove.tektroveadmin.brand;

import com.tektrove.tektroveadmin.paging.SearchRepository;
import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.entity.User;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrandRepository extends SearchRepository<Brand, Integer> {
    @Query("SELECT b FROM Brand b where b.name LIKE %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);
    Long countById(int id);

    Brand findByName(String name);
}
