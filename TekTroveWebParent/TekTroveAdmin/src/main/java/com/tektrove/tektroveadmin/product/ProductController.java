package com.tektrove.tektroveadmin.product;

import com.tektrove.tektroveadmin.brand.BrandService;
import com.tektrove.tektroveadmin.utils.ExporterUtil;
import com.tektrove.tektroveadmin.utils.FileUploadUtil;
import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.entity.product.Product;
import com.tektrovecommon.entity.product.ProductDetail;
import com.tektrovecommon.entity.product.ProductImage;
import com.tektrovecommon.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final BrandService brandService;
    private final String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&keyword=";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

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
                              String[] detailIDs,
                              String[] detailNames,
                              String[] detailValues,
                              String[] imageIDs,
                              String[] imageNames,
                              RedirectAttributes redirectAttributes) throws IOException {
        setMainImageName(fileImage, product);
        setExistingExtraImage(imageIDs, imageNames, product);
        setNewExtraImageNames(extraImage, product);
        setProductDetails(detailIDs, detailNames, detailValues, product);
        Product savedProduct = productService.save(product);
        saveUploadedImages(fileImage, extraImage, savedProduct);
        deleteExTraImagesRemovedOnForm(product);
        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return defaultRedirectURL;
    }

    private void setMainImageName(MultipartFile fileImage, Product product) {
        if (!fileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(fileImage.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    private void setExistingExtraImage(String[] imageIDs, String[] imageNames, Product product) {
        if (imageNames == null || imageIDs.length == 0) return;
        Set<ProductImage> images = new HashSet<>();
        for (int i = 0; i < imageIDs.length; i++) {
            int id = Integer.parseInt(imageIDs[i]);
            String name = imageNames[i];
            images.add(new ProductImage(id, name, product));
        }
        product.setImages(images);
    }

    private void setNewExtraImageNames(MultipartFile[] extraImage, Product product) {
        for (MultipartFile file : extraImage) {
            if (!file.isEmpty()) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                if (product.containsImageName(fileName)) {
                    product.addExtraImage(fileName);
                }
            }
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
                if (!file.isEmpty()) {
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    FileUploadUtil.saveFile(uploadDir, fileName, file);
                }
            }
        }
    }

    private void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        int numberOfDetails = detailNames.length;
        for (int i = 0; i < numberOfDetails; i++) {
            int id = Integer.parseInt(detailIDs[i]);
            String name = detailNames[i];
            String value = detailValues[i];
            if (!name.isEmpty() && !value.isEmpty()) {
                ProductDetail productDetail = (id != 0 ?
                        new ProductDetail(id, name, value, product) :
                        new ProductDetail(name, value, product));
                product.addDetail(productDetail);
            }
        }
    }

    private void deleteExTraImagesRemovedOnForm(Product product) {
        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);
        try {
            Files.list(dirPath).forEach(file -> {
                String fileName = file.toFile().getName();
                if (!product.containsImageName(fileName)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        LOGGER.error("Could not delete file: " + fileName);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Could not list directory: " + dirPath);
        }
    }

    @GetMapping("/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The product ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return defaultRedirectURL;
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            String productDir = "../product-images/" + id;
            String productExtraDir = "../product-images/" + id + "/extras";
            FileUploadUtil.removeDir(productDir);
            FileUploadUtil.removeDir(productExtraDir);
            redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully.");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);
            List<Brand> brands = brandService.listAllSorted();
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("brands", brands);
            model.addAttribute("isAddRichText", true);

            return "products/products_form";
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
            return "products/product_detail_modal";
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Product> products = productService.listAll();
        String[] headers = {"ID", "Name", "Alias", "Category", "Brand", "Cost", "Price", "Discount Percent", "Length", "Width", "Height", "Weight"};
        ExporterUtil.exportToCsv(products, headers, response);
    }
}
