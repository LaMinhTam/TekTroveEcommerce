package com.tektrove.tektrovecustomer.Product;

import com.tektrovecommon.entity.Category;
import com.tektrovecommon.entity.product.Product;
import com.tektrovecommon.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    public final int PRODUCT_PER_PAGE = 10;
    public final int SEARCH_RESULT_PER_PAGE = 10;
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> listProductsByCategory(int pageNum, Category category) {
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, Sort.by("name").ascending());
        return productRepository.findByCategoryId(category.getId(), pageable);
    }

    public Product getProductByAlias(String productAlias) throws ProductNotFoundException {
        return productRepository.findByAlias(productAlias).orElseThrow(() -> new ProductNotFoundException("Product not found with alias: " + productAlias));
    }

    public Page<Product> searchProducts(String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULT_PER_PAGE);
        return productRepository.searchProduct(keyword, pageable);
    }

}
