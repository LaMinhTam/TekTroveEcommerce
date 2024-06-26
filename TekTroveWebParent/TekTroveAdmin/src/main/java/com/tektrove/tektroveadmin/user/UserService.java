package com.tektrove.tektroveadmin.user;

import com.tektrove.tektroveadmin.paging.PagingAndSortingHelper;
import com.tektrovecommon.entity.Role;
import com.tektrovecommon.entity.User;
import com.tektrovecommon.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final static int USER_PER_PAGE = 5;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listByPage(pageNum, USER_PER_PAGE, userRepository);
    }

    public void updateUserEnabled(int id, boolean enabled) {
        userRepository.updateEnabled(id, enabled);
    }

    public User findUserById(int id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: #" + id));
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public User save(User user) {
        boolean isUpdating = user.getId() != null;
        if (isUpdating) {
            User existingUser = userRepository.findById(user.getId()).get();
            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }
            encodePassword(user);
        } else {
            encodePassword(user);
        }
        return userRepository.save(user);
    }

    private void encodePassword(User user) {
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
    }

    public void deleteUserById(int id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find user with ID " + id);
        }
        userRepository.deleteById(id);
    }

    public User findUserByEmail(String username) throws UserNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User updateAccount(User userInForm) {
        User userInDB = userRepository.findById(userInForm.getId()).get();

        if (!userInForm.getPassword().isEmpty()) {
            userInDB.setPassword(userInForm.getPassword());
            encodePassword(userInDB);
        }

        if (userInForm.getPhotos() != null) {
            userInDB.setPhotos(userInForm.getPhotos());
        }

        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());

        return userRepository.save(userInDB);
    }

    public boolean isEmailUnique(Integer id, String email) {
        Optional<User> userByEmail = userRepository.findByEmail(email);

        return (userByEmail.isEmpty() || userByEmail.get().getId().equals(id));
    }

}
