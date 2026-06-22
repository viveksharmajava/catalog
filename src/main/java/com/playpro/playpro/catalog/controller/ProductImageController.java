package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductImageInfoDto;
import com.playpro.playpro.catalog.productimage.ProductImageSize;
import com.playpro.playpro.catalog.service.ProductImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @GetMapping("/products/{productId}/images")
    public ResponseEntity<List<ProductImageInfoDto>> listImages(@PathVariable String productId) {
        return ResponseEntity.ok(productImageService.listImages(productId));
    }

    @PostMapping(value = "/products/{productId}/images/{size}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductImageInfoDto> uploadImage(@RequestHeader(value = "X-User", required = false) String xUser,
                                                           @PathVariable String productId,
                                                           @PathVariable String size,
                                                           @RequestPart("file") MultipartFile file) {
        ProductImageSize imageSize = ProductImageSize.fromPathSegment(size)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid image size. Allowed: small, medium, large, detail"));
        String principal = xUser == null ? "system" : xUser;
        return ResponseEntity.ok(productImageService.uploadImage(productId, imageSize, file, principal));
    }

    @GetMapping("/product-images/{productId}/{fileName}")
    public ResponseEntity<Resource> serveImage(@PathVariable String productId,
                                             @PathVariable String fileName) {
        Resource resource = productImageService.loadImageFile(productId, fileName);
        MediaType mediaType = resolveMediaType(fileName);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .body(resource);
    }

    private MediaType resolveMediaType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lower.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        if (lower.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }
        return MediaType.IMAGE_JPEG;
    }
}
