package com.tektrove.tektroveadmin.category;

import com.tektrovecommon.entity.Category;
import com.tektrovecommon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final String defaultRedirectUrl = "redirect:/categories/page/1?sortField=firstName&sortDir=asc";

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listAll() {
        return defaultRedirectUrl;
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
}
