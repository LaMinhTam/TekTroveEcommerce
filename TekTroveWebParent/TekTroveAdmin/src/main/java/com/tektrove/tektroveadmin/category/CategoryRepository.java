package com.tektrove.tektroveadmin.category;

import com.tektrovecommon.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
    Page<Category> search(String keyword, Pageable pageable);

    @Query("SELECT c FROM Category c where c.parent.id IS NULL")
    Page<Category> findRootCategories(Pageable pageable);

}
