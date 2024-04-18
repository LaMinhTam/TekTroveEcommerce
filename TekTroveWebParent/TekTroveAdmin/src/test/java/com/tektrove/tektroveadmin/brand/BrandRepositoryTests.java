package com.tektrove.tektroveadmin.brand;

import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {
    @Autowired
    private BrandRepository brandRepository;

//    @Test
//    public void testCreateBrand(){
//        Category category = Category.builder().id(6).build();
//        Brand acer = Brand.builder().name("Acer").logo("default.png").categories(Set.of(category)).build();
//        Brand savedBrand=brandRepository.save(acer);
//
//        assertThat(savedBrand).isNotNull();
//    }
//
//    @Test
//    public void testCreateMultiBrand(){
//        Category category = Category.builder().id(6).build();
//        Category category2 = Category.builder().id(2).build();
//        Brand acer = Brand.builder().name("Acer").logo("default.png").categories(Set.of(category)).build();
//        Brand samsung = Brand.builder().name("Samsumg").logo("default.png").categories(Set.of(category,category2)).build();
//        List<Brand> savedBrand = brandRepository.saveAll(List.of(acer, samsung));
//
//        assertThat(savedBrand.size()).isGreaterThan(0);
//    }

    @Test
    public void findAll(){
        List<Brand> brands = brandRepository.findAll();

        assertThat(brands.size()).isGreaterThan(0);
    }

    @Test
    public void testFindById(){
        Optional<Brand> brand = brandRepository.findById(1);
        assertThat(brand).isPresent();
    }

    @Test
    public void testUpdateName(){
        Brand acer = brandRepository.findById(1).get();
        acer.setName("Azer");
        Brand findAgain = brandRepository.findById(1).get();
        assertThat(findAgain.getName()).isEqualTo("Azer");
    }

    @Test
    public void testDelete(){
        brandRepository.deleteById(1);
        Optional<Brand> brand = brandRepository.findById(1);
        assertThat(brand).isEmpty();
    }
}
