package com.tektrove.tektroveadmin.product;

import com.tektrove.tektroveadmin.brand.BrandService;
import com.tektrove.tektroveadmin.utils.FileUploadUtil;
import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final BrandService brandService;
    private final String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&keyword=";

    public ProductController(ProductService productService, BrandService brandService) {
        this.productService = productService;
        this.brandService = brandService;
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

    @GetMapping("/new")
    public String newCategory(Model model) {
        List<Brand> brands = brandService.listAllSorted();
        Product product = Product.builder().enabled(true).inStock(true).build();
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("brands", brands);
        model.addAttribute("isAddRichText", true);
        return "products/products_form";
    }

    @PostMapping("/save")
    public String saveProduct(Product product,
                              MultipartFile fileImage,
                              MultipartFile[] extraImage,
                              String[] detailNames,
                              String[] detailValues,
                              RedirectAttributes redirectAttributes) throws IOException {
        setMainImageName(fileImage, product);
        setExtraImageNames(extraImage, product);
        setProductDetails(detailNames, detailValues, product);
        Product savedProduct = productService.save(product);
        saveUploadedImages(fileImage, extraImage, savedProduct);
        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return defaultRedirectURL;
    }

    private void setMainImageName(MultipartFile fileImage, Product product) {
        if (!fileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(fileImage.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    private void setExtraImageNames(MultipartFile[] extraImage, Product product) {
        for (MultipartFile file : extraImage) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            product.addExtraImage(fileName);
        }
    }

    private void saveUploadedImages(MultipartFile mainImage, MultipartFile[] extraImage, Product product) throws IOException {
        if (!mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImage.getOriginalFilename());
            String uploadDir = "../product-images/" + product.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImage);
        }
        if (extraImage.length > 0) {
            String uploadDir = "../product-images/" + product.getId() + "/extras";
            for (MultipartFile file : extraImage) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                FileUploadUtil.cleanDir(uploadDir);
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            }
        }
    }

    private void setProductDetails(String[] detailNames, String[] detailValues, Product product) {
        int numberOfDetails = detailNames.length;
        for (int i = 0; i < numberOfDetails; i++) {
            String name = detailNames[i];
            String value = detailValues[i];
            if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }
}
