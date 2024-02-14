package com.tektrove.tektroveadmin.user;

import com.tektrovecommon.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateRoles() {
        Role roleAdmin = new Role(1, "Admin", "Manage everything");
        Role roleSalesperson = new Role(2, "Salesperson", "manage product price, "
                + "customers, shipping, orders and sales report");

        Role roleEditor = new Role(3, "Editor", "manage categories, brands, "
                + "products, articles and menus");

        Role roleShipper = new Role(4, "Shipper", "view products, view orders "
                + "and update order status");

        Role roleAssistant = new Role(5, "Assistant", "manage questions and reviews");

        List<Role> roles = List.of(roleAdmin, roleSalesperson, roleEditor, roleShipper, roleAssistant);

        List<Role> savedRoles = roleRepository.saveAll(roles);
        assertThat(savedRoles.size()).isEqualTo(5);
    }
}
