package com.tektrove.tektroveadmin.product;

import com.tektrovecommon.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&keyword=";

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listAll(Model model) {
        return defaultRedirectURL;
    }

    @GetMapping("/page/{pageNum}")
    public String findByPage(Model model, @PathVariable int pageNum, String sortField, String sortDir, String keyword) {
        Page<Product> products = productService.listByPage(pageNum, sortField, sortDir, keyword);
        model.addAttribute("products", products.getContent());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleUrl", "products");

        int pageSize = products.getSize();

        long startCount = (pageNum - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;
        if (endCount > products.getTotalElements()) {
            endCount = products.getTotalElements();
        }
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", products.getTotalElements());

        return "products/products";
    }
}
