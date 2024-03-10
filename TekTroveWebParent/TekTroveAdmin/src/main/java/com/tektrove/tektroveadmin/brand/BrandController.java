package com.tektrove.tektroveadmin.brand;

import com.opencsv.CSVWriter;
import com.tektrove.tektroveadmin.category.CategoryService;
import com.tektrove.tektroveadmin.utils.ExporterUtil;
import com.tektrove.tektroveadmin.utils.FileUploadUtil;
import com.tektrove.tektroveadmin.utils.ResponseHeaderUtil;
import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.entity.Category;
import com.tektrovecommon.entity.product.Product;
import com.tektrovecommon.exception.BrandNotFoundException;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("brands")
public class BrandController {
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final String defaultRedirectURL = "redirect:/brands/page/1?sortField=name&sortDir=asc";

    public BrandController(BrandService brandService, CategoryService categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

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

    @GetMapping("/new")
    public String newBrand(Model model) {
        List<Category> categories = categoryService.getCategoriesUsedInForm();
        model.addAttribute("brand", new Brand());
        model.addAttribute("pageTitle", "Create New Brand");
        model.addAttribute("categories", categories);
        return "brands/brand_form";
    }

    @GetMapping("/edit/{id}")
    public String editBrand(Model model, RedirectAttributes redirectAttributes, @PathVariable int id) {
        try {
            Brand brand = brandService.findBrandById(id);
            List<Category> categories = categoryService.getCategoriesUsedInForm();

            model.addAttribute("pageTitle", "Edit Brand (Id: #" + id + ")");
            model.addAttribute("brand", brand);
            model.addAttribute("categories", categories);

            return "brands/brand_form";
        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @PostMapping("/save")
    public String saveBrand(Brand brand, RedirectAttributes redirectAttributes, @RequestParam("thumbnail") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            brand.setLogo(fileName);
            Brand savedUser = brandService.save(brand);

            String userPhotoDir = "../brand-logos/" + savedUser.getId();

            FileUploadUtil.cleanDir(userPhotoDir);
            FileUploadUtil.saveFile(userPhotoDir, fileName, multipartFile);
        } else {
            if (brand.getLogo().isEmpty()) {
                brand.setLogo(null);
            }
            brandService.save(brand);
        }
        redirectAttributes.addFlashAttribute("message", "The brand have been saved successfully");
        return "redirect:/brands/page/1?sortField=name&sortDir=true&keyword=" + brand.getName();
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(RedirectAttributes redirectAttributes, @PathVariable int id) {
        try {
            brandService.deleteBrandById(id);
            String brandLogosDir = "../brand-logos/" + id;
            FileUploadUtil.removeDir(brandLogosDir);
            redirectAttributes.addFlashAttribute("message", "delete brand #" + id + " successfully!");
        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/export/csv")
    public void exportBrandsCSV(HttpServletResponse response) throws IOException {
        List<Brand> brands = brandService.listAll();
        String[] headers = {"ID", "Name", "Categories"};
        ExporterUtil.exportToCsv(brands, headers, response);
    }

}
