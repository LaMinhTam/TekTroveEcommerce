package com.tektrove.tektroveadmin.brand;

import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.entity.User;
import com.tektrovecommon.exception.BrandNotFoundException;
import com.tektrovecommon.exception.CategoryNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    private static final int BRAND_PER_PAGE = 5;
    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> listAll() {
        return brandRepository.findAll();
    }

    public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, BRAND_PER_PAGE, sort);
        return keyword != null ? brandRepository.findAll(keyword, pageable) : brandRepository.findAll(pageable);
    }

    public Brand findBrandById(int id) throws BrandNotFoundException {
        return brandRepository.findById(id).orElseThrow(() -> new BrandNotFoundException("Could not find brand with id #" + id));
    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public void deleteBrandById(int id) throws BrandNotFoundException {
        Long countById = brandRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new BrandNotFoundException("Could not find any category with ID " + id);
        }

        brandRepository.deleteById(id);
    }

    public boolean isBrandNameUnique(Integer id, String name) {
        Brand existingBrandWithName = brandRepository.findByName(name);
        return existingBrandWithName == null || existingBrandWithName.getId().equals(id);
    }
}
