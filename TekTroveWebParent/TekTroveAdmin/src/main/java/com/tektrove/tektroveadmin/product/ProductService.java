package com.tektrove.tektroveadmin.product;

import com.tektrove.tektroveadmin.paging.PagingAndSortingHelper;
import com.tektrovecommon.entity.Category;
import com.tektrovecommon.entity.product.Product;
import com.tektrovecommon.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private static final int PRODUCT_PER_PAGE = 5;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listAll() {
        return productRepository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper, String categoryId) {
        Sort sort = Sort.by(helper.getSortField());
        sort = helper.getSortDir().equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);
        //when the category id is null or 0, it means we are searching for all products
        //else it means we are searching for products in a specific category, and with -categoryId- in the categoryIds column
        Page<Product> products = categoryId == null || Integer.parseInt(categoryId) == 0 ?
                productRepository.findAll(helper.getKeyword(), pageable) :
                productRepository.findByCategoryIdAndKeyword("-" + categoryId + "-", helper.getKeyword(), pageable);
        helper.updateModelAttributes(pageNum, products);
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }
        product.setUpdatedTime(new Date());
        String alias = product.getAlias().isEmpty()
                ? product.getName().replaceAll(" ", "-")
                : product.getAlias().replaceAll(" ", "-");
        product.setAlias(alias);
        return productRepository.save(product);
    }

    public void updateProductPricingInformation(Product productInForm) {
        Product productInDB = productRepository.findById(productInForm.getId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());
        productRepository.save(productInDB);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Product productByName = productRepository.findByName(name);

        if (productByName != null && (isCreatingNew || !productByName.getId().equals(id))) {
            return "Duplicate";
        }

        return "OK";
    }

    public void updateProductEnabledStatus(Integer id, boolean enabled) {
        productRepository.updateEnabledStatus(id, enabled);
    }

    public void deleteProduct(Integer id) throws ProductNotFoundException {
        int countById = productRepository.countById(id);
        if (countById == 0) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
        productRepository.deleteById(id);
    }

    public Product get(int id) throws ProductNotFoundException {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Could not find any product with ID " + id));
    }
}
