package com.tektrove.tektroveadmin.user;

import com.tektrove.tektroveadmin.security.TekTroveUserDetails;
import com.tektrove.tektroveadmin.utils.FileUploadUtil;
import com.tektrovecommon.entity.User;
import com.tektrovecommon.exception.UserNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;

@Controller
@RequestMapping("/account")
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String viewDetail(@AuthenticationPrincipal TekTroveUserDetails loggedUser, Model model) throws UserNotFoundException {
        User user = userService.findUserByEmail(loggedUser.getUsername());
        model.addAttribute("user", user);
        return "users/account_form";
    }

    @PostMapping("update")
    public String saveDetail(RedirectAttributes redirectAttributes, User user, @AuthenticationPrincipal TekTroveUserDetails loggedUser, @RequestParam("thumbnail") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            user.setPhotos(fileName);
            User savedUser = userService.updateAccount(user);

            String userPhotoDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(userPhotoDir);
            FileUploadUtil.saveFile(userPhotoDir, fileName, multipartFile);
        }else{
            if(user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
            userService.updateAccount(user);
        }

        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setLastName(user.getLastName());

        redirectAttributes.addFlashAttribute("message", "Your account details have been updated.");

        return "redirect:/account";
    }
}
