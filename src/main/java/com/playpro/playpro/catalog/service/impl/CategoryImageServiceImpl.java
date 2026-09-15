package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.EntityImageInfoDto;
import com.playpro.playpro.catalog.entity.category.ProductCategory;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.media.ImageFileSupport;
import com.playpro.playpro.catalog.repository.ProductCategoryRepository;
import com.playpro.playpro.catalog.service.CategoryImageService;
import com.playpro.playpro.catalog.storage.ImageFolders;
import com.playpro.playpro.catalog.storage.ImageObjectStore;
import com.playpro.playpro.catalog.storage.StoredImageObject;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class CategoryImageServiceImpl implements CategoryImageService {

    private static final String IMAGE_PREFIX = "image.";

    private final ProductCategoryRepository categoryRepository;
    private final ImageObjectStore imageObjectStore;

    public CategoryImageServiceImpl(ProductCategoryRepository categoryRepository,
                                    ImageObjectStore imageObjectStore) {
        this.categoryRepository = categoryRepository;
        this.imageObjectStore = imageObjectStore;
    }

    @Override
    @Transactional(readOnly = true)
    public EntityImageInfoDto getImageInfo(String categoryId) {
        ProductCategory category = loadCategory(categoryId);
        String url = category.getCategoryImageUrl();
        boolean uploaded = StringUtils.hasText(url);
        String fileName = null;
        if (uploaded && imageObjectStore.servesViaCatalogApi()) {
            fileName = imageObjectStore.findFileName(ImageFolders.CATEGORY, categoryId, IMAGE_PREFIX).orElse(null);
        } else if (uploaded) {
            fileName = extractFileName(url);
        }
        return buildImageInfo(url, fileName, uploaded, categoryId);
    }

    @Override
    public EntityImageInfoDto uploadImage(String categoryId, MultipartFile file, String principal) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        ProductCategory category = loadCategory(categoryId);
        String extension = ImageFileSupport.resolveExtension(file);
        String fileName = IMAGE_PREFIX + extension;
        byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read category image", ex);
        }

        StoredImageObject stored = imageObjectStore.store(
                ImageFolders.CATEGORY,
                categoryId,
                fileName,
                IMAGE_PREFIX,
                content,
                ImageFileSupport.resolveMediaType(fileName).toString());

        category.setCategoryImageUrl(stored.getPublicUrl());
        category.applyAuditOnUpdate(principal);
        categoryRepository.save(category);

        return buildImageInfo(stored.getPublicUrl(), stored.getFileName(), true, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadImageFile(String categoryId, String fileName) {
        return imageObjectStore.load(ImageFolders.CATEGORY, categoryId, fileName);
    }

    private ProductCategory loadCategory(String categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private EntityImageInfoDto buildImageInfo(String url, String fileName, boolean uploaded, String categoryId) {
        EntityImageInfoDto dto = new EntityImageInfoDto();
        dto.setUrl(url);
        dto.setFileName(fileName);
        dto.setUploaded(uploaded);
        if (uploaded && StringUtils.hasText(fileName)) {
            dto.setStoragePath(ImageFolders.CATEGORY + "/"
                    + ImageFileSupport.sanitizeEntityId(categoryId) + "/" + fileName);
        }
        return dto;
    }

    private static String extractFileName(String url) {
        int slash = url.lastIndexOf('/');
        return slash >= 0 && slash < url.length() - 1 ? url.substring(slash + 1) : null;
    }
}
