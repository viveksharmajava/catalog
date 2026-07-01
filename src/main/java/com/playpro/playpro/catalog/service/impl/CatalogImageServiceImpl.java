package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.EntityImageInfoDto;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalog;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.media.ImageFileSupport;
import com.playpro.playpro.catalog.media.MediaImageProperties;
import com.playpro.playpro.catalog.repository.ProdCatalogRepository;
import com.playpro.playpro.catalog.service.CatalogImageService;
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
public class CatalogImageServiceImpl implements CatalogImageService {

    private static final String IMAGE_PREFIX = "logo.";

    private final ProdCatalogRepository prodCatalogRepository;
    private final MediaImageProperties properties;
    private Path storageRoot;

    public CatalogImageServiceImpl(ProdCatalogRepository prodCatalogRepository,
                                   MediaImageProperties properties) {
        this.prodCatalogRepository = prodCatalogRepository;
        this.properties = properties;
    }

    @PostConstruct
    public void initStorage() throws IOException {
        storageRoot = Paths.get(properties.getCatalogStoragePath()).toAbsolutePath().normalize();
        Files.createDirectories(storageRoot);
    }

    @Override
    @Transactional(readOnly = true)
    public EntityImageInfoDto getImageInfo(String prodCatalogId) {
        ProdCatalog catalog = loadCatalog(prodCatalogId);
        Path catalogDir = storageRoot.resolve(ImageFileSupport.sanitizeEntityId(prodCatalogId));
        String fileName = findExistingFileName(catalogDir).orElse(null);
        Path filePath = fileName != null ? catalogDir.resolve(fileName) : null;
        return buildImageInfo(catalog, fileName, filePath);
    }

    @Override
    public EntityImageInfoDto uploadImage(String prodCatalogId, MultipartFile file, String principal) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        ProdCatalog catalog = loadCatalog(prodCatalogId);
        String extension = ImageFileSupport.resolveExtension(file);
        String fileName = IMAGE_PREFIX + extension;
        Path catalogDir = storageRoot.resolve(ImageFileSupport.sanitizeEntityId(prodCatalogId));
        Path target = catalogDir.resolve(fileName);

        try {
            Files.createDirectories(catalogDir);
            deleteExistingImages(catalogDir);
            file.transferTo(target.toFile());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store catalog image", ex);
        }

        String publicUrl = ImageFileSupport.buildPublicUrl(
                properties.getPublicBaseUrl(), "catalog-images", prodCatalogId, fileName);
        catalog.setHeaderLogo(publicUrl);
        prodCatalogRepository.save(catalog);

        return buildImageInfo(catalog, fileName, target);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadImageFile(String prodCatalogId, String fileName) {
        ImageFileSupport.validateFileName(fileName);
        Path filePath = storageRoot
                .resolve(ImageFileSupport.sanitizeEntityId(prodCatalogId))
                .resolve(fileName)
                .normalize();
        if (!filePath.startsWith(storageRoot)) {
            throw new IllegalArgumentException("Invalid image path");
        }
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ResourceNotFoundException("Catalog image not found: " + fileName);
        }
        return new FileSystemResource(filePath);
    }

    private ProdCatalog loadCatalog(String prodCatalogId) {
        return prodCatalogRepository.findById(prodCatalogId)
                .orElseThrow(() -> new ResourceNotFoundException("Catalog not found: " + prodCatalogId));
    }

    private EntityImageInfoDto buildImageInfo(ProdCatalog catalog, String fileName, Path filePath) {
        EntityImageInfoDto dto = new EntityImageInfoDto();
        dto.setUrl(catalog.getHeaderLogo());
        if (filePath != null && Files.exists(filePath)) {
            dto.setFileName(fileName);
            dto.setStoragePath(storageRoot.relativize(filePath).toString().replace('\\', '/'));
            dto.setUploaded(true);
        } else {
            dto.setUploaded(catalog.getHeaderLogo() != null && !catalog.getHeaderLogo().trim().isEmpty());
        }
        return dto;
    }

    private java.util.Optional<String> findExistingFileName(Path catalogDir) {
        if (!Files.exists(catalogDir)) {
            return java.util.Optional.empty();
        }
        try (Stream<Path> paths = Files.list(catalogDir)) {
            return paths.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(IMAGE_PREFIX))
                    .findFirst();
        } catch (IOException ex) {
            return java.util.Optional.empty();
        }
    }

    private void deleteExistingImages(Path catalogDir) throws IOException {
        if (!Files.exists(catalogDir)) {
            return;
        }
        try (Stream<Path> paths = Files.list(catalogDir)) {
            for (Path existing : paths.filter(Files::isRegularFile).toArray(Path[]::new)) {
                if (existing.getFileName().toString().toLowerCase(Locale.ROOT).startsWith(IMAGE_PREFIX)) {
                    Files.deleteIfExists(existing);
                }
            }
        }
    }
}
