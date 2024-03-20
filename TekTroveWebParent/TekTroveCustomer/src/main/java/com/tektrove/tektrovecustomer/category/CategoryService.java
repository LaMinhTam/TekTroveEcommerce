package com.tektrove.tektrovecustomer.category;

import com.tektrovecommon.entity.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> listNoChildrenCategories() {
        List<Category> listEnabledCategories = categoryRepository.findByEnabledIsTrueOrderByNameAsc();
        List<Category> listNoChildrenCategories = new ArrayList<>();
        listEnabledCategories.forEach(category -> {
            if (category.getChildren().isEmpty()) {
                listNoChildrenCategories.add(category);
            }
        });
        return listNoChildrenCategories;
    }

    public Category getCategoryByAlias(String categoryAlias) {
        return categoryRepository.findByAlias(categoryAlias);
    }

    public List<Category> getAllParentCategories(Category category) {
        List<Category> categoriesHierarchical = new ArrayList<>();
        Category parent = category.getParent();
        while (parent != null) {
            categoriesHierarchical.add(0, parent);
            parent = parent.getParent();
        }
        categoriesHierarchical.add(category);
        return categoriesHierarchical;
    }
}
