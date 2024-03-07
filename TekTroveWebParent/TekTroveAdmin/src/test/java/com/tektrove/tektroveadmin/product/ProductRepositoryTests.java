package com.tektrove.tektroveadmin.product;

import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.entity.Category;
import com.tektrovecommon.entity.product.Product;
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
@Rollback(value = false)
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
        product.setMainImage("samsung-galaxy-s21.jpg");
        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProducts() {
        Iterable<Product> products = productRepository.findAll();
        products.forEach(System.out::println);
    }

    @Test
    public void testFindById() {
        Product product = productRepository.findById(2).get();
        assertThat(product).isNotNull();
        System.out.println(product);
    }

    @Test
    public void testUpdateProduct() {
        Integer productId = 2;
        Product product = productRepository.findById(productId).get();
        product.setPrice(1200);
        productRepository.save(product);
        Product updatedProduct = testEntityManager.find(Product.class, productId);
        assertThat(updatedProduct.getPrice()).isEqualTo(1200);
    }

    @Test
    public void testDeleteProduct() {
        Integer productId = 2;
        productRepository.deleteById(productId);
        Product product = testEntityManager.find(Product.class, productId);
        assertThat(product).isNull();
    }

    @Test
    public void testSaveProductWithImages() {
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();

        product.setMainImage("main image.jpg");
        product.addExtraImage("extra image 1.png");
        product.addExtraImage("extra_image_2.png");
        product.addExtraImage("extra-image3.png");

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getImages().size()).isEqualTo(3);
    }

    @Test
    public void testSaveProductWithDetails() {
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();

        product.addDetail("Display", "Super AMOLED");
        product.addDetail("OS", "Android 11");
        product.addDetail("RAM", "8GB");
        product.addDetail("Storage", "128GB");

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getDetails()).isNotEmpty();
    }
}
