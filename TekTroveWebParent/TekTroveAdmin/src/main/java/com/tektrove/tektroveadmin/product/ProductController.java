package com.tektrove.tektroveadmin.product;

import com.tektrove.tektroveadmin.brand.BrandService;
import com.tektrove.tektroveadmin.category.CategoryService;
import com.tektrove.tektroveadmin.paging.PagingAndSortingHelper;
import com.tektrove.tektroveadmin.paging.PagingAndSortingParam;
import com.tektrove.tektroveadmin.security.TekTroveUserDetails;
import com.tektrove.tektroveadmin.utils.ExporterUtil;
import com.tektrove.tektroveadmin.utils.FileUploadUtil;
import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.entity.Category;
import com.tektrovecommon.entity.product.Product;
import com.tektrovecommon.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&keyword=&categoryId=0";

    public ProductController(ProductService productService, BrandService brandService, CategoryService categoryService) {
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listAll() {
        return defaultRedirectURL;
    }

    @GetMapping("/page/{pageNum}")
    public String findByPage(
            @PagingAndSortingParam(moduleName = "products") PagingAndSortingHelper helper,
            Model model, @PathVariable int pageNum, String categoryId) {
        productService.listByPage(pageNum, helper, categoryId);
        model.addAttribute("categoryId", categoryId);
        List<Category> categories = categoryService.getCategoriesUsedInForm();
        model.addAttribute("categories", categories);
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
    public String saveProduct(@AuthenticationPrincipal TekTroveUserDetails loggedUser,
                              Product product,
                              MultipartFile fileImage,
                              MultipartFile[] extraImage,
                              String[] detailIDs,
                              String[] detailNames,
                              String[] detailValues,
                              String[] imageIDs,
                              String[] imageNames,
                              RedirectAttributes redirectAttributes) throws IOException {
        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor") && loggedUser.hasRole("Salesperson")) {
            productService.updateProductPricingInformation(product);
            redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
            return defaultRedirectURL;
        }
        ProductSaveHelper.setMainImageName(fileImage, product);
        ProductSaveHelper.setExistingExtraImage(imageIDs, imageNames, product);
        ProductSaveHelper.setNewExtraImageNames(extraImage, product);
        ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);
        Product savedProduct = productService.save(product);
        ProductSaveHelper.saveUploadedImages(fileImage, extraImage, savedProduct);
        ProductSaveHelper.deleteExTraImagesRemovedOnForm(product);
        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return defaultRedirectURL;
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
    public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes, @AuthenticationPrincipal TekTroveUserDetails loggedUser) {
        try {
            Product product = productService.get(id);
            List<Brand> brands = brandService.listAllSorted();
            boolean isReadOnlyForSalesperson = false;

            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("brands", brands);
            model.addAttribute("isAddRichText", true);

            if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
                if (loggedUser.hasRole("Salesperson")) {
                    isReadOnlyForSalesperson = true;
                }
            }
            model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);

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
