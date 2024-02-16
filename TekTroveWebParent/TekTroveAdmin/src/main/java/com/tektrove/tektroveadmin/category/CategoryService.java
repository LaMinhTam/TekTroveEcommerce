package com.tektrove.tektroveadmin.category;

import com.tektrovecommon.entity.Category;
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
}