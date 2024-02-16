package com.tektrove.tektroveadmin.category;

import com.tektrovecommon.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
    Page<Category> search(String keyword, Pageable pageable);

    @Query("SELECT c FROM Category c where c.parent.id IS NULL")
    Page<Category> findRootCategories(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
    List<Category> findRootCategories(Sort name);

    public Category findByName(String name);

    public Category findByAlias(String alias);


    @Query("UPDATE Category SET enabled = ?2 WHERE id = ?1")
    @Modifying
    void updateEnabledState(int id, boolean enabled);

    Long countById(int id);
}
