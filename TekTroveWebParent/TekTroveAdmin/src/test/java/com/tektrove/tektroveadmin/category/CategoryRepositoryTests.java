package com.tektrove.tektroveadmin.category;

import com.tektrovecommon.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testCreateCategory() {
        Category category = Category.builder().name("Computer").alias("computer").image("image.png").build();
        categoryRepository.save(category);
        assertThat(category).isNotNull();
    }

    @Test
    public void testCreateChildCategory() {
        Category parentCategory = Category.builder().id(1).build();
        Category categoryLaptop = Category.builder().name("laptop").alias("laptop").image("image.png").parent(parentCategory).build();
        Category categoryComputerComponent = Category.builder().name("Computer Component").alias("computercomponent").image("image.png").parent(parentCategory).build();
        List<Category> savedCategories = categoryRepository.saveAll(List.of(categoryLaptop, categoryComputerComponent));
        assertThat(savedCategories.size()).isGreaterThan(0);
    }

    @Test
    public void testListAllCategoriesWithHierarchy() {
        List<Category> categories = categoryRepository.findAll();

        for (Category category : categories) {
            if (category.getParent() == null) {
                System.out.println(formatCategoryWithHierarchy(category, 0));
            }
        }
    }

    private String formatCategoryWithHierarchy(Category category, int depth) {
        StringBuilder formattedCategory = new StringBuilder();

        for (int i = 0; i < depth; i++) {
            formattedCategory.append("--");
        }
        formattedCategory.append(category.getName());

        if (!category.getChildren().isEmpty()) {
            for (Category child : category.getChildren()) {
                formattedCategory.append("\n").append(formatCategoryWithHierarchy(child, depth + 1));
            }
        }

        return formattedCategory.toString();
    }
}
