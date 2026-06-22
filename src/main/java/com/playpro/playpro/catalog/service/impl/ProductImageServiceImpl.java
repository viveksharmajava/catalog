package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProductImageInfoDto;
import com.playpro.playpro.catalog.entity.product.Product;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.productimage.ProductImageProperties;
import com.playpro.playpro.catalog.productimage.ProductImageSize;
import com.playpro.playpro.catalog.repository.ProductRepository;
import com.playpro.playpro.catalog.service.ProductImageService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

@Service
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    ));

    private final ProductRepository productRepository;
    private final ProductImageProperties properties;
    private Path storageRoot;

    public ProductImageServiceImpl(ProductRepository productRepository, ProductImageProperties properties) {
        this.productRepository = productRepository;
        this.properties = properties;
    }

    @PostConstruct
    public void initStorage() throws IOException {
        storageRoot = Paths.get(properties.getStoragePath()).toAbsolutePath().normalize();
        Files.createDirectories(storageRoot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageInfoDto> listImages(String productId) {
        Product product = loadProduct(productId);
        List<ProductImageInfoDto> images = new ArrayList<>();
        for (ProductImageSize size : ProductImageSize.values()) {
            images.add(buildImageInfo(product, size));
        }
        return images;
    }

    @Override
    public ProductImageInfoDto uploadImage(String productId, ProductImageSize size, MultipartFile file, String principal) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        Product product = loadProduct(productId);
        String extension = resolveExtension(file);
        String fileName = size.getPathSegment() + "." + extension;
        Path productDir = storageRoot.resolve(sanitizeProductId(productId));
        Path target = productDir.resolve(fileName);

        try {
            Files.createDirectories(productDir);
            deleteExistingSizeFiles(productDir, size);
            file.transferTo(target.toFile());
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store image file", ex);
        }

        String publicUrl = buildPublicUrl(productId, fileName);
        applyImageUrl(product, size, publicUrl);
        product.applyAuditOnUpdate(principal);
        productRepository.save(product);

        return buildImageInfo(product, size, fileName, target);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadImageFile(String productId, String fileName) {
        validateFileName(fileName);
        Path filePath = storageRoot.resolve(sanitizeProductId(productId)).resolve(fileName).normalize();
        if (!filePath.startsWith(storageRoot)) {
            throw new IllegalArgumentException("Invalid image path");
        }
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ResourceNotFoundException("Image not found: " + fileName);
        }
        return new FileSystemResource(filePath);
    }

    private Product loadProduct(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }

    private ProductImageInfoDto buildImageInfo(Product product, ProductImageSize size) {
        Path productDir = storageRoot.resolve(sanitizeProductId(product.getProductId()));
        String fileName = findExistingFileName(productDir, size).orElse(null);
        Path filePath = fileName != null ? productDir.resolve(fileName) : null;
        return buildImageInfo(product, size, fileName, filePath);
    }

    private ProductImageInfoDto buildImageInfo(Product product, ProductImageSize size, String fileName, Path filePath) {
        ProductImageInfoDto dto = new ProductImageInfoDto();
        dto.setSize(size.getPathSegment());
        dto.setLabel(size.getLabel());
        dto.setUrl(getImageUrl(product, size));
        if (filePath != null && Files.exists(filePath)) {
            dto.setFileName(fileName);
            dto.setStoragePath(storageRoot.relativize(filePath).toString().replace('\\', '/'));
            dto.setUploaded(true);
        } else {
            dto.setUploaded(false);
        }
        return dto;
    }

    private String getImageUrl(Product product, ProductImageSize size) {
        switch (size) {
            case SMALL:
                return product.getSmallImageUrl();
            case MEDIUM:
                return product.getMediumImageUrl();
            case LARGE:
                return product.getLargeImageUrl();
            case DETAIL:
                return product.getDetailImageUrl();
            default:
                return null;
        }
    }

    private void applyImageUrl(Product product, ProductImageSize size, String url) {
        switch (size) {
            case SMALL:
                product.setSmallImageUrl(url);
                break;
            case MEDIUM:
                product.setMediumImageUrl(url);
                break;
            case LARGE:
                product.setLargeImageUrl(url);
                break;
            case DETAIL:
                product.setDetailImageUrl(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported image size: " + size);
        }
    }

    private String buildPublicUrl(String productId, String fileName) {
        String base = properties.getPublicBaseUrl();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/catalog/product-images/" + sanitizeProductId(productId) + "/" + fileName;
    }

    private void deleteExistingSizeFiles(Path productDir, ProductImageSize size) throws IOException {
        if (!Files.exists(productDir)) {
            return;
        }
        String prefix = size.getPathSegment() + ".";
        try (Stream<Path> paths = Files.list(productDir)) {
            for (Path existing : paths.filter(Files::isRegularFile).toArray(Path[]::new)) {
                if (existing.getFileName().toString().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    Files.deleteIfExists(existing);
                }
            }
        }
    }

    private java.util.Optional<String> findExistingFileName(Path productDir, ProductImageSize size) {
        if (!Files.exists(productDir)) {
            return java.util.Optional.empty();
        }
        String prefix = size.getPathSegment() + ".";
        try (Stream<Path> paths = Files.list(productDir)) {
            return paths.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix))
                    .findFirst();
        } catch (IOException ex) {
            return java.util.Optional.empty();
        }
    }

    private String resolveExtension(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
            if (ALLOWED_EXTENSIONS.contains(ext)) {
                return "jpeg".equals(ext) ? "jpg" : ext;
            }
        }

        String contentType = file.getContentType();
        if (contentType != null) {
            switch (contentType.toLowerCase(Locale.ROOT)) {
                case "image/jpeg":
                    return "jpg";
                case "image/png":
                    return "png";
                case "image/gif":
                    return "gif";
                case "image/webp":
                    return "webp";
                default:
                    break;
            }
        }

        throw new IllegalArgumentException("Unsupported image type. Allowed: jpg, png, gif, webp");
    }

    private String sanitizeProductId(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("productId is required");
        }
        String sanitized = productId.trim().replaceAll("[^A-Za-z0-9._-]", "_");
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("Invalid productId");
        }
        return sanitized;
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("Invalid file name");
        }
        int dot = fileName.lastIndexOf('.');
        if (dot <= 0) {
            throw new IllegalArgumentException("Invalid file name");
        }
        String ext = fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(ext) && !"jpeg".equals(ext)) {
            throw new IllegalArgumentException("Unsupported image type");
        }
    }
}
