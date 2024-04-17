package com.tektrove.tektrovecustomer.product;

import com.tektrove.tektrovecustomer.category.CategoryService;
import com.tektrovecommon.entity.Category;
import com.tektrovecommon.entity.product.Product;
import com.tektrovecommon.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {
    public final CategoryService categoryService;
    private final ProductService productService;

    public ProductController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/c/{categoryAlias}")
    public String viewCategoryFirstPage(Model model, @PathVariable String categoryAlias) {
        return viewCategory(model, categoryAlias, 1);
    }

    @GetMapping("/c/{categoryAlias}/page/{pageNum}")
    public String viewCategory(Model model, @PathVariable String categoryAlias, @PathVariable int pageNum) {
        Category category = categoryService.getCategoryByAlias(categoryAlias);
        if (category == null || !category.isEnabled()) {
            return "error/404";
        }
        Page<Product> pageInfo = productService.listProductsByCategory(pageNum, category);
        model.addAttribute("products", pageInfo.getContent());
        model.addAttribute("categoriesHierarchical", categoryService.getAllParentCategories(category));
        model.addAttribute("category", category);

        model.addAttribute("moduleUrl", "products");
        long startCount = (long) (pageNum - 1) * productService.PRODUCT_PER_PAGE + 1;
        long endCount = startCount + productService.PRODUCT_PER_PAGE - 1;
        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("pageTitle", category.getName());
        return "products/product_by_category";
    }

    @GetMapping("/p/{productAlias}")
    public String viewProduct(Model model, @PathVariable String productAlias) {
        try {
            Product product = productService.getProductByAlias(productAlias);
            model.addAttribute("product", product);
            model.addAttribute("categoriesHierarchical", categoryService.getAllParentCategories(product.getCategory()));
            model.addAttribute("pageTitle", product.getName());
            return "products/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/search")
    public String searchFirstPage(String keyword) {
        return "redirect:/search/page/1?keyword=" + keyword;
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchProducts(Model model, @PathVariable int pageNum, String keyword) {
        Page<Product> pageInfo = productService.searchProducts(keyword, pageNum);
        model.addAttribute("products", pageInfo.getContent());
        model.addAttribute("keyword", keyword);
        long startCount = (long) (pageNum - 1) * productService.SEARCH_RESULT_PER_PAGE + 1;
        long endCount = startCount + productService.SEARCH_RESULT_PER_PAGE - 1;
        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }
        model.addAttribute("pageTitle", keyword);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        return "products/product_search_result";
    }
}
