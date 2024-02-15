package com.tektrove.tektroveadmin.user;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.opencsv.CSVWriter;
import com.tektrove.tektroveadmin.utils.FileUploadUtil;
import com.tektrovecommon.entity.Role;
import com.tektrovecommon.entity.User;
import com.tektrovecommon.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public String listByPage(Model model, @PathVariable int pageNum, @RequestParam String sortField, @RequestParam String sortDir, @RequestParam(required = false) String keyword) {
        Page<User> users = userService.listByPage(pageNum, sortField, sortDir, keyword);

        model.addAttribute("users", users.getContent());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleUrl", "users");


        int pageSize = users.getSize();

        long startCount = (pageNum - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;
        if (endCount > users.getTotalElements()) {
            endCount = users.getTotalElements();
        }
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", users.getTotalElements());

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
        return "redirect:/users/page/1?sortField=firstName&sortDir=true";
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
        setResponseHeader(response, "text/csv", ".csv", "users_");
        List<User> users = userService.listAll();

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
            writer.writeNext(new String[]{"ID", "Email", "Enabled", "First Name", "Last Name", "Roles"});

            for (User user : users) {
                writer.writeNext(new String[]{
                        String.valueOf(user.getId()),
                        user.getEmail(),
                        String.valueOf(user.isEnabled()),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRolesAsString()
                });
            }
        }
    }

    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        setResponseHeader(response, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx", "users_");

        List<User> users = userService.listAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Users");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Email", "Enabled", "First Name", "Last Name", "Roles"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getEmail());
                row.createCell(2).setCellValue(user.isEnabled());
                row.createCell(3).setCellValue(user.getFirstName());
                row.createCell(4).setCellValue(user.getLastName());
                row.createCell(5).setCellValue(user.getRolesAsString());
            }

            workbook.write(out);
            response.getOutputStream().write(out.toByteArray());
        }
    }

    @GetMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response) throws IOException {
        setResponseHeader(response, "application/pdf", ".pdf", "users");
        List<User> users = userService.listAll();

        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            Table table = new Table(new float[]{1.2f, 3.5f, 3.0f, 3.0f, 3.0f, 1.7f});
            table.addHeaderCell("ID");
            table.addHeaderCell("Email");
            table.addHeaderCell("Enabled");
            table.addHeaderCell("First Name");
            table.addHeaderCell("Last Name");
            table.addHeaderCell("Roles");

            for (User user : users) {
                table.addCell(String.valueOf(user.getId()));
                table.addCell(user.getEmail());
                table.addCell(user.isEnabled() ? "Enabled" : "Disabled");
                table.addCell(user.getFirstName());
                table.addCell(user.getLastName());
                table.addCell(user.getRolesAsString());
            }

            document.add(table);
        }
    }

    public void setResponseHeader(HttpServletResponse response, String contentType,
                                  String extension, String prefix) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormatter.format(new Date());
        String fileName = prefix + "_" + timestamp + extension;

        response.setContentType(contentType);

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=\"" + fileName + "\"";
        response.setHeader(headerKey, headerValue);
    }
}
