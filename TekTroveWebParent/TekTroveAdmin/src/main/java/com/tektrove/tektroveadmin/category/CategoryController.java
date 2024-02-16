package com.tektrove.tektroveadmin.category;

import com.tektrove.tektroveadmin.utils.FileUploadUtil;
import com.tektrovecommon.entity.Category;
import com.tektrovecommon.exception.CategoryNotFoundException;
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
@RequestMapping("categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final String defaultRedirectURL = "redirect:/categories/page/1?sortField=firstName&sortDir=asc";

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listAll() {
        return defaultRedirectURL;
    }


    @GetMapping("/page/{pageNum}")
    public String listByPage(Model model, @PathVariable int pageNum, String sortDir, String keyword) {
        CategoryPageInfo pageInfo = new CategoryPageInfo();
        Page<Category> categories = categoryService.listByPage(pageInfo, pageNum, sortDir, keyword);

        model.addAttribute("categories", categories.getContent());

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleUrl", "categories");

        long startCount = (long) (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageInfo.getTotalElements());

        return "categories/categories";
    }

    @GetMapping("/new")
    public String newCategory(Model model) {
        List<Category> categories = categoryService.getCategoriesUsedInForm();

        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Create New Category");
        model.addAttribute("categories", categories);
        return "categories/categories_form";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(Model model, RedirectAttributes redirectAttributes, @PathVariable int id) {
        try {
            Category category = categoryService.findCategoryById(id);
            List<Category> categories = categoryService.getCategoriesUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Edit User (Id: #" + id + ")");
            model.addAttribute("categories", categories);

            return "categories/categories_form";
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @PostMapping("/save")
    public String saveUser(Category category, RedirectAttributes redirectAttributes, @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            category.setImage(fileName);
            Category savedCategory = categoryService.save(category);

            String categoryPhotoDir = "../category-images/" + savedCategory.getId();

            FileUploadUtil.cleanDir(categoryPhotoDir);
            FileUploadUtil.saveFile(categoryPhotoDir, fileName, multipartFile);
        } else {
            if (category.getImage().isEmpty()) {
                category.setImage(null);
            }
            categoryService.save(category);
        }
        redirectAttributes.addFlashAttribute("message", "The user have been saved successfully");
        return "redirect:/categories/page/1?sortField=firstName&sortDir=true&keyword=" + category.getName();
    }

    @GetMapping("/{id}/enabled/{enabled}")
    public String updateUserEnabledStatus(RedirectAttributes redirectAttributes, @PathVariable int id, @PathVariable boolean enabled) {
        categoryService.updateCategoryEnabled(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "the category ID #" + id + " has been " + status);
        return defaultRedirectURL;
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(RedirectAttributes redirectAttributes, @PathVariable int id) {
        try {
            categoryService.deleteCategoryById(id);
            String categoryPhotosDir = "category-images/" + id;
            FileUploadUtil.removeDir(categoryPhotosDir);
            redirectAttributes.addFlashAttribute("message", "delete category #" + id + " successfully!");
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/export/csv")
    public void exportCSV(HttpServletResponse response) throws IOException {
        List<Category> categories = categoryService.getCategoriesUsedInForm();
        CategoryExporter.exportCSV(categories, response);
    }
}
