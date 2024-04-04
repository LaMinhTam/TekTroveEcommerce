package com.tektrove.tektroveadmin.user;

import com.tektrove.tektroveadmin.paging.SearchRepository;
import com.tektrovecommon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends SearchRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ',"
            + " u.lastName) LIKE %?1%")
    Page<User> findAll(String keyword, Pageable pageable);

    @Query("UPDATE User SET enabled = ?2 WHERE id = ?1")
    @Modifying
    void updateEnabled(int id, boolean enabled);

    Long countById(int id);

    Optional<User> findByEmail(String username);
}
