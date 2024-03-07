package com.tektrove.tektroveadmin.product;

import com.tektrovecommon.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private static final int PRODUCT_PER_PAGE = 5;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listAll() {
        return productRepository.findAll();
    }

    public Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);
        return keyword != null ? productRepository.findAll(keyword, pageable) : productRepository.findAll(pageable);
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

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Product productByName = productRepository.findByName(name);

        if (productByName != null && (isCreatingNew || !productByName.getId().equals(id))) {
            return "Duplicate";
        }

        return "OK";
    }

}
