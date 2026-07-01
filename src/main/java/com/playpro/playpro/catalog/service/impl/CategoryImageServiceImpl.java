package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.EntityImageInfoDto;
import com.playpro.playpro.catalog.entity.category.ProductCategory;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.media.ImageFileSupport;
import com.playpro.playpro.catalog.media.MediaImageProperties;
import com.playpro.playpro.catalog.repository.ProductCategoryRepository;
import com.playpro.playpro.catalog.service.CategoryImageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Stream;

@Service
@Transactional
public class CategoryImageServiceImpl implements CategoryImageService {

    private static final String IMAGE_PREFIX = "image.";

    private final ProductCategoryRepository categoryRepository;
    private final MediaImageProperties properties;
    private Path storageRoot;

    public CategoryImageServiceImpl(ProductCategoryRepository categoryRepository,
                                    MediaImageProperties properties) {
        this.categoryRepository = categoryRepository;
        this.properties = properties;
    }

    @PostConstruct
    public void initStorage() throws IOException {
        storageRoot = Paths.get(properties.getCategoryStoragePath()).toAbsolutePath().normalize();
        Files.createDirectories(storageRoot);
    }

    @Override
    @Transactional(readOnly = true)
    public EntityImageInfoDto getImageInfo(String categoryId) {
        ProductCategory category = loadCategory(categoryId);
        Path categoryDir = storageRoot.resolve(ImageFileSupport.sanitizeEntityId(categoryId));
        String fileName = findExistingFileName(categoryDir).orElse(null);
        Path filePath = fileName != null ? categoryDir.resolve(fileName) : null;
        return buildImageInfo(category, fileName, filePath);
    }

    @Override
    public EntityImageInfoDto uploadImage(String categoryId, MultipartFile file, String principal) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        ProductCategory category = loadCategory(categoryId);
        String extension = ImageFileSupport.resolveExtension(file);
        String fileName = IMAGE_PREFIX + extension;
        Path categoryDir = storageRoot.resolve(ImageFileSupport.sanitizeEntityId(categoryId));
        Path target = categoryDir.resolve(fileName);

        try {
            Files.createDirectories(categoryDir);
            deleteExistingImages(categoryDir);
            file.transferTo(target.toFile());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store category image", ex);
        }

        String publicUrl = ImageFileSupport.buildPublicUrl(
                properties.getPublicBaseUrl(), "category-images", categoryId, fileName);
        category.setCategoryImageUrl(publicUrl);
        category.applyAuditOnUpdate(principal);
        categoryRepository.save(category);

        return buildImageInfo(category, fileName, target);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadImageFile(String categoryId, String fileName) {
        ImageFileSupport.validateFileName(fileName);
        Path filePath = storageRoot
                .resolve(ImageFileSupport.sanitizeEntityId(categoryId))
                .resolve(fileName)
                .normalize();
        if (!filePath.startsWith(storageRoot)) {
            throw new IllegalArgumentException("Invalid image path");
        }
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ResourceNotFoundException("Category image not found: " + fileName);
        }
        return new FileSystemResource(filePath);
    }

    private ProductCategory loadCategory(String categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private EntityImageInfoDto buildImageInfo(ProductCategory category, String fileName, Path filePath) {
        EntityImageInfoDto dto = new EntityImageInfoDto();
        dto.setUrl(category.getCategoryImageUrl());
        if (filePath != null && Files.exists(filePath)) {
            dto.setFileName(fileName);
            dto.setStoragePath(storageRoot.relativize(filePath).toString().replace('\\', '/'));
            dto.setUploaded(true);
        } else {
            dto.setUploaded(category.getCategoryImageUrl() != null && !category.getCategoryImageUrl().trim().isEmpty());
        }
        return dto;
    }

    private java.util.Optional<String> findExistingFileName(Path categoryDir) {
        if (!Files.exists(categoryDir)) {
            return java.util.Optional.empty();
        }
        try (Stream<Path> paths = Files.list(categoryDir)) {
            return paths.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(IMAGE_PREFIX))
                    .findFirst();
        } catch (IOException ex) {
            return java.util.Optional.empty();
        }
    }

    private void deleteExistingImages(Path categoryDir) throws IOException {
        if (!Files.exists(categoryDir)) {
            return;
        }
        try (Stream<Path> paths = Files.list(categoryDir)) {
            for (Path existing : paths.filter(Files::isRegularFile).toArray(Path[]::new)) {
                if (existing.getFileName().toString().toLowerCase(Locale.ROOT).startsWith(IMAGE_PREFIX)) {
                    Files.deleteIfExists(existing);
                }
            }
        }
    }
}
