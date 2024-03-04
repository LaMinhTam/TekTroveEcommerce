package com.tektrove.tektroveadmin.product;

import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.entity.Category;
import com.tektrovecommon.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void testCreateProduct() {
        Brand brand = testEntityManager.find(Brand.class, 1);
        Category category = testEntityManager.find(Category.class, 1);
        Product product = new Product();
        product.setName("Samsung Galaxy S21");
        product.setAlias("samsung-galaxy-s21");
        product.setShortDescription("New Samsung Galaxy S21");
        product.setFullDescription("New Samsung Galaxy S21 with 5G");
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());
        product.setBrand(brand);
        product.setCategory(category);
        product.setCost(800);
        product.setPrice(1000);
        product.setDiscountPercent(10);
        product.setLength(5);
        product.setWidth(3);
        product.setHeight(1);
        product.setWeight(0.5f);
        product.setInStock(true);
        product.setEnabled(true);
        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProducts(){
        Iterable<Product> products = productRepository.findAll();
        products.forEach(System.out::println);
    }

    @Test
    public void testFindById(){
        Product product = productRepository.findById(2).get();
        assertThat(product).isNotNull();
        System.out.println(product);
    }

    @Test
    public void testUpdateProduct(){
        Integer productId = 2;
        Product product = productRepository.findById(productId).get();
        product.setPrice(1200);
        productRepository.save(product);
        Product updatedProduct = testEntityManager.find(Product.class, productId);
        assertThat(updatedProduct.getPrice()).isEqualTo(1200);
    }

    @Test
    public void testDeleteProduct(){
        Integer productId = 2;
        productRepository.deleteById(productId);
        Product product = testEntityManager.find(Product.class, productId);
        assertThat(product).isNull();
    }
}
