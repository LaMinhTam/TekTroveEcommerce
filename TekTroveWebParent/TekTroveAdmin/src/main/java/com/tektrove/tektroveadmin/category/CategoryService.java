package com.tektrove.tektroveadmin.category;

import com.tektrovecommon.entity.Category;
import com.tektrovecommon.exception.CategoryNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.*;

@Service
@Transactional
public class CategoryService {

    public static final int ROOT_CATEGORIES_PER_PAGE = 4;

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
        Sort sort = Sort.by("name");
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> categoryPage;

        if (keyword != null) {
            categoryPage = categoryRepository.search(keyword, pageable);
        } else {
            categoryPage = categoryRepository.findRootCategories(pageable);
        }

        pageInfo.setTotalPages(categoryPage.getTotalPages());
        pageInfo.setTotalElements(categoryPage.getTotalElements());

        return convertToHierarchicalPage(categoryPage, sortDir);
    }

    private Page<Category> convertToHierarchicalPage(Page<Category> categoryPage, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();
        categoryPage.forEach(rootCategory -> {
            hierarchicalCategories.add(rootCategory);
            hierarchicalCategories.addAll(getSubHierarchicalCategories(rootCategory, sortDir, 1));
        });
        return new PageImpl<>(hierarchicalCategories, categoryPage.getPageable(), categoryPage.getTotalElements());
    }

    public List<Category> getCategoriesUsedInForm() {
        List<Category> rootCategories = categoryRepository.findRootCategories(Sort.by("name").ascending());
        List<Category> hierarchicalCategories = new ArrayList<>();

        rootCategories.forEach(rootCategory -> {
            hierarchicalCategories.add(rootCategory);
            hierarchicalCategories.addAll(getSubHierarchicalCategories(rootCategory, "name", 1));
        });

        return hierarchicalCategories;
    }

    private List<Category> getSubHierarchicalCategories(Category parent, String sortDir, int subLevel) {
        List<Category> subHierarchicalCategories = new ArrayList<>();
        SortedSet<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        children.forEach(child -> {
            Category childCopy = Category.copyCategory(child);
            childCopy.setName(StringUtils.repeat("--", subLevel) + child.getName());
            subHierarchicalCategories.add(childCopy);
            subHierarchicalCategories.addAll(getSubHierarchicalCategories(childCopy, sortDir, subLevel + 1));
        });
        return subHierarchicalCategories;
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        Comparator<Category> comparator = Comparator.comparing(Category::getName);
        if ("desc".equals(sortDir)) {
            comparator = comparator.reversed();
        }
        SortedSet<Category> sortedChildren = new TreeSet<>(comparator);
        sortedChildren.addAll(children);
        return sortedChildren;
    }

    public Category findCategoryById(int id) throws CategoryNotFoundException {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Could not find category with id #" + id));
    }

    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = categoryRepository.findByName(name);

        if (isCreatingNew) {
            if (categoryByName != null) {
                return "DuplicateName";
            } else {
                Category categoryByAlias = categoryRepository.findByAlias(alias);
                if (categoryByAlias != null) {
                    return "DuplicateAlias";
                }
            }
        } else {
            if (categoryByName != null && !Objects.equals(categoryByName.getId(), id)) {
                return "DuplicateName";
            }

            Category categoryByAlias = categoryRepository.findByAlias(alias);
            if (categoryByAlias != null && !Objects.equals(categoryByAlias.getId(), id)) {
                return "DuplicateAlias";
            }

        }

        return "OK";
    }

    public void updateCategoryEnabled(int id, boolean enabled) {
        categoryRepository.updateEnabledState(id, enabled);
    }

    public void deleteCategoryById(int id) throws CategoryNotFoundException {
        Long countById = categoryRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }

        categoryRepository.deleteById(id);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }
}