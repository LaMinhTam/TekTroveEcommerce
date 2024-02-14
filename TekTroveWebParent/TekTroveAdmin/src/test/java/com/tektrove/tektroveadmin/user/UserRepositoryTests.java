package com.tektrove.tektroveadmin.user;

import com.tektrovecommon.entity.Role;
import com.tektrovecommon.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUser() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User user = new User("laminhtam@gmail.com", "Minh Tam", "La", "123456");
        user.addRole(roleAdmin);

        User savedUser=userRepository.save(user);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListUser(){
        List<User> users=userRepository.findAll();
        assertThat(users.size()).isGreaterThan(0);
    }

    @Test
    public void testGetUserById(){
        User user = userRepository.findById(1).get();
        assertThat(user.getId()).isGreaterThan(0);
    }

    @Test
    public void testUpdateUser(){
        User user = userRepository.findById(1).get();
        user.setEnabled(true);
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    public void testUpdateRole(){
        User user = userRepository.findById(1).get();
        user.getRoles().remove(new Role(1));
        user.addRole(new Role(2));
        user.addRole(new Role(3));

        assertThat(user.getRoles().size()).isEqualTo(2);
    }

    @Test
    public void testDeleteUser(){
        User user = userRepository.findById(1).get();
        userRepository.delete(user);
        Optional<User> findById = userRepository.findById(1);
        assertThat(findById).isEmpty();
    }
}
