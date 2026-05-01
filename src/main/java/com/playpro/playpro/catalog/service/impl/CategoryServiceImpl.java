package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.CategoryDto;
import com.playpro.playpro.catalog.entity.Category;
import com.playpro.playpro.catalog.mapper.CategoryMapper;
import com.playpro.playpro.catalog.repository.CategoryRepository;
import com.playpro.playpro.catalog.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public CategoryDto createCategory(CategoryDto dto, String principal) {
        Category c = CategoryMapper.toEntity(dto);
        if (dto.getParentId() != null) {
            repository.findById(dto.getParentId()).ifPresent(c::setParent);
        }
        c.setCreatedBy(principal);
        Category saved = repository.save(c);
        return CategoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto dto, String principal) {
        Category existing = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setDescription(dto.getDescription());
        existing.setActive(dto.getActive() == null ? existing.getActive() : dto.getActive());
        if (dto.getParentId() != null) {
            repository.findById(dto.getParentId()).ifPresent(existing::setParent);
        } else {
            existing.setParent(null);
        }
        Category saved = repository.save(existing);
        return CategoryMapper.toDto(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long id) {
        return repository.findById(id).map(CategoryMapper::toDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoryTree() {
        List<Category> all = repository.findAll();
        return all.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }
}
