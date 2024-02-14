package com.tektrove.tektroveadmin.user;

import com.tektrovecommon.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
