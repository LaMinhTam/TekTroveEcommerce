package com.tektrove.tektroveadmin.user;

import com.tektrovecommon.entity.User;
import com.tektrovecommon.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/checkEmail")
    public ResponseEntity<Boolean> checkEmailExists(Integer id, String email) {
        boolean  isUnique = userService.isEmailUnique(id, email);

        return ResponseEntity.ok(isUnique);
    }
}
