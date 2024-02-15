package com.tektrove.tektroveadmin.user;

import com.tektrovecommon.entity.Role;
import com.tektrovecommon.entity.User;
import com.tektrovecommon.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final static int USER_PER_PAGE = 5;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);
        return keyword != null ? userRepository.findAll(keyword, pageable) : userRepository.findAll(pageable);
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
        } else {
            encodePassword(user);
        }
        return userRepository.save(user);
    }

    private void encodePassword(User user) {
        String encodePassword = user.getPassword();
        user.setPassword(encodePassword);
    }

    public void deleteUserById(int id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find user with ID " + id);
        }
        userRepository.deleteById(id);
    }
}
