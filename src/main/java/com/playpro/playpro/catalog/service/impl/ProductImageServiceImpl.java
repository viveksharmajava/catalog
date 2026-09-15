package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProductImageImportItemDto;
import com.playpro.playpro.catalog.dto.ProductImageImportResultDto;
import com.playpro.playpro.catalog.dto.ProductImageInfoDto;
import com.playpro.playpro.catalog.entity.product.Product;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.media.ImageFileSupport;
import com.playpro.playpro.catalog.productimage.ProductImageSize;
import com.playpro.playpro.catalog.repository.ProductRepository;
import com.playpro.playpro.catalog.service.ProductImageService;
import com.playpro.playpro.catalog.storage.ImageFolders;
import com.playpro.playpro.catalog.storage.ImageObjectStore;
import com.playpro.playpro.catalog.storage.StoredImageObject;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    ));

    private static final Set<String> ZIP_IMPORT_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png"
    ));

    private final ProductRepository productRepository;
    private final ImageObjectStore imageObjectStore;

    public ProductImageServiceImpl(ProductRepository productRepository, ImageObjectStore imageObjectStore) {
        this.productRepository = productRepository;
        this.imageObjectStore = imageObjectStore;
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

        String extension = resolveExtension(file);
        try {
            return storeImageBytes(productId, size, file.getBytes(), extension, principal);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read uploaded image file", ex);
        }
    }

    @Override
    public ProductImageImportResultDto importImagesZip(MultipartFile zipFile, String principal) {
        if (zipFile == null || zipFile.isEmpty()) {
            throw new IllegalArgumentException("ZIP file is required");
        }
        String originalName = zipFile.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase(Locale.ROOT).endsWith(".zip")) {
            throw new IllegalArgumentException("Only .zip files are supported for image import");
        }

        ProductImageImportResultDto result = new ProductImageImportResultDto();
        int total = 0;
        int imported = 0;
        int skipped = 0;
        int failed = 0;

        try (InputStream inputStream = zipFile.getInputStream();
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                String sourcePath = normalizeZipPath(entry.getName());
                if (!StringUtils.hasText(sourcePath) || shouldSkipZipEntry(sourcePath)) {
                    continue;
                }

                total++;
                ProductImageImportItemDto item = new ProductImageImportItemDto();
                item.setSourcePath(sourcePath);

                try {
                    ParsedImagePath parsed = parseImagePath(sourcePath);
                    item.setProductId(parsed.productId);
                    item.setImageType(parsed.size.getPathSegment());

                    if (!productRepository.existsById(parsed.productId)) {
                        throw new IllegalArgumentException("Product not found: " + parsed.productId);
                    }

                    byte[] content = StreamUtils.copyToByteArray(zipInputStream);
                    if (content.length == 0) {
                        throw new IllegalArgumentException("Image file is empty");
                    }

                    ProductImageInfoDto stored = storeImageBytes(
                            parsed.productId,
                            parsed.size,
                            content,
                            parsed.extension,
                            principal
                    );
                    item.setStoredFileName(stored.getFileName());
                    item.setUrl(stored.getUrl());
                    item.setStatus("IMPORTED");
                    item.setMessage("Mapped to " + parsed.productId + "/" + parsed.size.getPathSegment());
                    imported++;
                } catch (Exception ex) {
                    item.setStatus("FAILED");
                    item.setMessage(ex.getMessage() != null ? ex.getMessage() : "Import failed");
                    failed++;
                }

                result.getItems().add(item);
                zipInputStream.closeEntry();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read ZIP archive", ex);
        }

        result.setTotalEntries(total);
        result.setImported(imported);
        result.setSkipped(skipped);
        result.setFailed(failed);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadImageFile(String productId, String fileName) {
        return imageObjectStore.load(ImageFolders.PRODUCT, productId, fileName);
    }

    private ProductImageInfoDto storeImageBytes(String productId,
                                                ProductImageSize size,
                                                byte[] content,
                                                String extension,
                                                String principal) {
        Product product = loadProduct(productId);
        String normalizedExt = "jpeg".equals(extension) ? "jpg" : extension;
        String fileName = size.getPathSegment() + "." + normalizedExt;
        String prefix = size.getPathSegment() + ".";
        MediaType mediaType = ImageFileSupport.resolveMediaType(fileName);

        StoredImageObject stored = imageObjectStore.store(
                ImageFolders.PRODUCT,
                productId,
                fileName,
                prefix,
                content,
                mediaType.toString());

        applyImageUrl(product, size, stored.getPublicUrl());
        product.applyAuditOnUpdate(principal);
        productRepository.save(product);

        return buildImageInfo(product, size, stored.getFileName(), stored.getPublicUrl(), true);
    }

    private ParsedImagePath parseImagePath(String sourcePath) {
        String[] parts = sourcePath.split("/");
        if (parts.length < 3) {
            throw new IllegalArgumentException(
                    "Invalid path. Expected <product_id>/<image_type>/<image_name>.jpg|png");
        }

        String fileName = parts[parts.length - 1];
        String imageType = parts[parts.length - 2];
        String productId = parts[parts.length - 3];

        if (!StringUtils.hasText(productId) || !StringUtils.hasText(imageType) || !StringUtils.hasText(fileName)) {
            throw new IllegalArgumentException(
                    "Invalid path. Expected <product_id>/<image_type>/<image_name>.jpg|png");
        }

        ProductImageSize size = ProductImageSize.fromPathSegment(imageType)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid image type '" + imageType + "'. Allowed: small, medium, large, detail, original"));

        int dot = fileName.lastIndexOf('.');
        if (dot <= 0 || dot == fileName.length() - 1) {
            throw new IllegalArgumentException("Image file must include an extension (.jpg or .png)");
        }
        String extension = fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!ZIP_IMPORT_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Unsupported image type '" + extension + "'. Allowed: jpg, png");
        }

        return new ParsedImagePath(productId.trim(), size, "jpeg".equals(extension) ? "jpg" : extension);
    }

    private String normalizeZipPath(String entryName) {
        if (entryName == null) {
            return "";
        }
        String normalized = entryName.replace('\\', '/').trim();
        while (normalized.startsWith("./")) {
            normalized = normalized.substring(2);
        }
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    private boolean shouldSkipZipEntry(String sourcePath) {
        String lower = sourcePath.toLowerCase(Locale.ROOT);
        return lower.startsWith("__macosx/")
                || lower.contains("/__macosx/")
                || lower.endsWith(".ds_store")
                || lower.endsWith("/thumbs.db");
    }

    private Product loadProduct(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }

    private ProductImageInfoDto buildImageInfo(Product product, ProductImageSize size) {
        String url = getImageUrl(product, size);
        boolean uploaded = StringUtils.hasText(url);
        String fileName = null;
        if (uploaded && imageObjectStore.servesViaCatalogApi()) {
            fileName = imageObjectStore.findFileName(
                    ImageFolders.PRODUCT, product.getProductId(), size.getPathSegment() + ".").orElse(null);
        } else if (uploaded) {
            fileName = extractFileName(url);
        }
        return buildImageInfo(product, size, fileName, url, uploaded);
    }

    private ProductImageInfoDto buildImageInfo(Product product,
                                               ProductImageSize size,
                                               String fileName,
                                               String url,
                                               boolean uploaded) {
        ProductImageInfoDto dto = new ProductImageInfoDto();
        dto.setSize(size.getPathSegment());
        dto.setLabel(size.getLabel());
        dto.setUrl(url);
        dto.setFileName(fileName);
        dto.setUploaded(uploaded);
        if (uploaded && StringUtils.hasText(fileName)) {
            dto.setStoragePath(ImageFolders.PRODUCT + "/"
                    + ImageFileSupport.sanitizeEntityId(product.getProductId()) + "/" + fileName);
        }
        return dto;
    }

    private static String extractFileName(String url) {
        if (!StringUtils.hasText(url)) {
            return null;
        }
        int slash = url.lastIndexOf('/');
        return slash >= 0 && slash < url.length() - 1 ? url.substring(slash + 1) : null;
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

    private static final class ParsedImagePath {
        private final String productId;
        private final ProductImageSize size;
        private final String extension;

        private ParsedImagePath(String productId, ProductImageSize size, String extension) {
            this.productId = productId;
            this.size = size;
            this.extension = extension;
        }
    }
}
