package com.tektrove.tektroveadmin.brand;

import com.tektrovecommon.entity.Brand;
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
@RequestMapping("brands")
public class BrandController {
    @Autowired
    private BrandService brandService;

    private final String defaultRedirectURL = "redirect:/brands/page/1?sortField=name&sortDir=asc";

    @GetMapping
    public String listAll() {
        return defaultRedirectURL;
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(Model model, @PathVariable int pageNum, @RequestParam String sortField, @RequestParam String sortDir, @RequestParam(required = false) String keyword) {
        Page<Brand> brands = brandService.listByPage(pageNum, sortField, sortDir, keyword);

        model.addAttribute("brands", brands.getContent());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleUrl", "brands");

        int pageSize = brands.getSize();

        long startCount = (pageNum - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;
        if (endCount > brands.getTotalElements()) {
            endCount = brands.getTotalElements();
        }
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", brands.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", brands.getTotalElements());

        return "brands/brands";
    }

}
