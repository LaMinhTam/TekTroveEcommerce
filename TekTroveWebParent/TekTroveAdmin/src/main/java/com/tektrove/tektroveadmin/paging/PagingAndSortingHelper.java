package com.tektrove.tektroveadmin.paging;

import com.tektrovecommon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagingAndSortingHelper {
    ModelAndViewContainer model;
    String moduleName;
    String sortField;
    String sortDir;
    String keyword;

    public PagingAndSortingHelper(ModelAndViewContainer model, String moduleName, String sortField, String sortDir, String keyword) {
        this.model = model;
        this.moduleName = moduleName;
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.keyword = keyword;
    }

    public void updateModelAttributes(int pageNum, Page<?> page) {
        model.addAttribute(moduleName, page.getContent());
        model.addAttribute("moduleUrl", moduleName);
        int pageSize = page.getSize();

        long startCount = (pageNum - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
    }

    public void listByPage(int pageNum, int pageSize, SearchRepository<?, Integer> searchRepository) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<?> page;
        if (keyword != null) {
            page = searchRepository.findAll(keyword, pageable);
        }else{
            page = searchRepository.findAll(pageable);
        }

        updateModelAttributes(pageNum, page);
    }

    public String getSortField() {
        return sortField;
    }

    public String getSortDir() {
        return sortDir;
    }

    public String getKeyword() {
        return keyword;
    }
}
