package com.tektrove.tektroveadmin.user;

import com.itextpdf.text.DocumentException;
import com.tektrove.tektroveadmin.paging.PagingAndSortingHelper;
import com.tektrove.tektroveadmin.paging.PagingAndSortingParam;
import com.tektrove.tektroveadmin.utils.ExporterUtil;
import com.tektrove.tektroveadmin.utils.FileUploadUtil;
import com.tektrovecommon.entity.Role;
import com.tektrovecommon.entity.User;
import com.tektrovecommon.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("users")
public class UserController {
    private final UserService userService;
    private final String defaultRedirectURL = "redirect:/users/page/1?sortField=firstName&sortDir=asc";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listAll() {
        return defaultRedirectURL;
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(moduleName = "users") PagingAndSortingHelper helper,
            @PathVariable int pageNum) {
        userService.listByPage(pageNum, helper);
        return "users/users";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        User user = new User();
        user.setEnabled(true);

        List<Role> roles = userService.getRoles();

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Create New User");
        model.addAttribute("roles", roles);
        return "users/user_form";
    }

    @GetMapping("/edit/{id}")
    public String editUser(Model model, RedirectAttributes redirectAttributes, @PathVariable int id) {
        try {
            User user = userService.findUserById(id);
            List<Role> roles = userService.getRoles();

            model.addAttribute("pageTitle", "Edit User (Id: #" + id + ")");
            model.addAttribute("user", user);
            model.addAttribute("roles", roles);

            return "users/user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @PostMapping("/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("thumbnail") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            user.setPhotos(fileName);
            User savedUser = userService.save(user);

            String userPhotoDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(userPhotoDir);
            FileUploadUtil.saveFile(userPhotoDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
            userService.save(user);
        }
        redirectAttributes.addFlashAttribute("message", "The user have been saved successfully");
        return "redirect:/users/page/1?sortField=firstName&sortDir=true&keyword=" + user.getEmail();
    }

    @GetMapping("/{id}/enabled/{enabled}")
    public String updateUserEnabledStatus(RedirectAttributes redirectAttributes, @PathVariable int id, @PathVariable boolean enabled) {
        userService.updateUserEnabled(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "the user ID #" + id + " has been " + status);
        return defaultRedirectURL;
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(RedirectAttributes redirectAttributes, @PathVariable int id) {
        try {
            userService.deleteUserById(id);
            String userPhotosDir = "user-photos/" + id;
            FileUploadUtil.removeDir(userPhotosDir);
            redirectAttributes.addFlashAttribute("message", "delete user #" + id + " successfully!");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/export/csv")
    public void exportCSV(HttpServletResponse response) throws IOException {
        List<User> users = userService.listAll();
        String[] headers = {"ID", "Email", "Enabled", "First Name", "Last Name", "Roles"};
        ExporterUtil.exportToCsv(users, headers, response);
    }

    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        List<User> users = userService.listAll();
        String[] headers = {"ID", "Email", "Enabled", "First Name", "Last Name", "Roles"};
        ExporterUtil.exportToExcel(users, headers, response);
    }

    @GetMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response) throws IOException, DocumentException {
        List<User> users = userService.listAll();
        String[] headers = {"ID", "Email", "Enabled", "First Name", "Last Name", "Roles"};
        ExporterUtil.exportToPdf(users, headers, response);
    }
}
