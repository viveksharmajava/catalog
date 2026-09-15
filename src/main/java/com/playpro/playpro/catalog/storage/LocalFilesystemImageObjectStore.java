package com.playpro.playpro.catalog.storage;

import com.playpro.playpro.catalog.media.ImageFileSupport;
import com.playpro.playpro.catalog.media.MediaImageProperties;
import com.playpro.playpro.catalog.productimage.ProductImageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(name = "catalog.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFilesystemImageObjectStore implements ImageObjectStore {

    private final ProductImageProperties productImageProperties;
    private final MediaImageProperties mediaImageProperties;

    private Path productRoot;
    private Path categoryRoot;
    private Path catalogRoot;

    public LocalFilesystemImageObjectStore(ProductImageProperties productImageProperties,
                                           MediaImageProperties mediaImageProperties) {
        this.productImageProperties = productImageProperties;
        this.mediaImageProperties = mediaImageProperties;
    }

    @PostConstruct
    public void init() throws IOException {
        productRoot = Paths.get(productImageProperties.getStoragePath()).toAbsolutePath().normalize();
        categoryRoot = Paths.get(mediaImageProperties.getCategoryStoragePath()).toAbsolutePath().normalize();
        catalogRoot = Paths.get(mediaImageProperties.getCatalogStoragePath()).toAbsolutePath().normalize();
        Files.createDirectories(productRoot);
        Files.createDirectories(categoryRoot);
        Files.createDirectories(catalogRoot);
    }

    @Override
    public StoredImageObject store(String folder,
                                   String entityId,
                                   String fileName,
                                   String fileNamePrefix,
                                   byte[] content,
                                   String contentType) {
        ImageFileSupport.validateFileName(fileName);
        String safeId = ImageFileSupport.sanitizeEntityId(entityId);
        Path dir = rootFor(folder).resolve(safeId);
        Path target = dir.resolve(fileName).normalize();
        if (!target.startsWith(rootFor(folder))) {
            throw new IllegalArgumentException("Invalid image path");
        }
        try {
            Files.createDirectories(dir);
            deleteMatching(dir, fileNamePrefix);
            Files.copy(new ByteArrayInputStream(content), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store image file", ex);
        }
        String url = publicUrl(folder, entityId, fileName);
        return new StoredImageObject(objectKey(folder, safeId, fileName), fileName, url);
    }

    @Override
    public Optional<String> findFileName(String folder, String entityId, String fileNamePrefix) {
        Path dir = rootFor(folder).resolve(ImageFileSupport.sanitizeEntityId(entityId));
        if (!Files.exists(dir)) {
            return Optional.empty();
        }
        String prefix = fileNamePrefix == null ? "" : fileNamePrefix.toLowerCase(Locale.ROOT);
        try (Stream<Path> paths = Files.list(dir)) {
            return paths.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix))
                    .findFirst();
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Resource load(String folder, String entityId, String fileName) {
        ImageFileSupport.validateFileName(fileName);
        Path root = rootFor(folder);
        Path filePath = root.resolve(ImageFileSupport.sanitizeEntityId(entityId)).resolve(fileName).normalize();
        if (!filePath.startsWith(root)) {
            throw new IllegalArgumentException("Invalid image path");
        }
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new com.playpro.playpro.catalog.exception.ResourceNotFoundException("Image not found: " + fileName);
        }
        return new FileSystemResource(filePath);
    }

    @Override
    public String publicUrl(String folder, String entityId, String fileName) {
        String base = publicBaseFor(folder);
        return ImageFileSupport.buildPublicUrl(base, folder, entityId, fileName);
    }

    @Override
    public boolean servesViaCatalogApi() {
        return true;
    }

    private Path rootFor(String folder) {
        if (ImageFolders.PRODUCT.equals(folder)) {
            return productRoot;
        }
        if (ImageFolders.CATEGORY.equals(folder)) {
            return categoryRoot;
        }
        if (ImageFolders.CATALOG.equals(folder)) {
            return catalogRoot;
        }
        throw new IllegalArgumentException("Unknown image folder: " + folder);
    }

    private String publicBaseFor(String folder) {
        if (ImageFolders.PRODUCT.equals(folder)) {
            return productImageProperties.getPublicBaseUrl();
        }
        return mediaImageProperties.getPublicBaseUrl();
    }

    private static String objectKey(String folder, String entityId, String fileName) {
        return folder + "/" + entityId + "/" + fileName;
    }

    private static void deleteMatching(Path dir, String fileNamePrefix) throws IOException {
        if (!Files.exists(dir) || fileNamePrefix == null || fileNamePrefix.isEmpty()) {
            return;
        }
        String prefix = fileNamePrefix.toLowerCase(Locale.ROOT);
        try (Stream<Path> paths = Files.list(dir)) {
            for (Path existing : paths.filter(Files::isRegularFile).toArray(Path[]::new)) {
                if (existing.getFileName().toString().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    Files.deleteIfExists(existing);
                }
            }
        }
    }
}
