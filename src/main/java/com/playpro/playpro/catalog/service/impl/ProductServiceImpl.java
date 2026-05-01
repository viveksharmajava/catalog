package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.CategoryDto;
import com.playpro.playpro.catalog.dto.ProductDto;
import com.playpro.playpro.catalog.entity.Category;
import com.playpro.playpro.catalog.entity.Product;
import com.playpro.playpro.catalog.entity.ProductCategoryMap;
import com.playpro.playpro.catalog.mapper.CategoryMapper;
import com.playpro.playpro.catalog.mapper.ProductMapper;
import com.playpro.playpro.catalog.repository.CategoryRepository;
import com.playpro.playpro.catalog.repository.ProductCategoryMapRepository;
import com.playpro.playpro.catalog.repository.ProductRepository;
import com.playpro.playpro.catalog.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryMapRepository pcmRepository;

    public ProductServiceImpl(ProductRepository repository, CategoryRepository categoryRepository, ProductCategoryMapRepository pcmRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.pcmRepository = pcmRepository;
    }

    @Override
    public ProductDto createProduct(ProductDto dto, String principal) {
        Product p = ProductMapper.toEntity(dto);
        p.setCreatedBy(principal);
        if (p.getStatus() == null) p.setStatus("DRAFT");
        Product saved = repository.save(p);
        return ProductMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProduct(Long id) {
        return repository.findById(id).map(ProductMapper::toDto).orElse(null);
    }

    @Override
    public void addCategoryToProduct(Long productId, Long categoryId, String principal) {
        Product product = repository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category not found"));
        // idempotent
        if (pcmRepository.findByProductIdAndCategoryId(productId, categoryId).isPresent()) return;
        ProductCategoryMap pcm = new ProductCategoryMap();
        pcm.setProductId(product.getId());
        pcm.setCategoryId(category.getId());
        pcmRepository.save(pcm);
    }

    @Override
    public void removeCategoryFromProduct(Long productId, Long categoryId, String principal) {
        // idempotent
        pcmRepository.deleteByProductIdAndCategoryId(productId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesForProduct(Long productId) {
        List<ProductCategoryMap> maps = pcmRepository.findByProductId(productId);
        return maps.stream()
                .map(m -> categoryRepository.findById(m.getCategoryId()).orElse(null))
                .filter(c -> c != null)
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }
}
