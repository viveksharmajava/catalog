package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.EntityImageInfoDto;
import com.playpro.playpro.catalog.media.ImageFileSupport;
import com.playpro.playpro.catalog.service.CategoryImageService;
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

@RestController
@RequestMapping("/catalog")
public class CategoryImageController {

    private final CategoryImageService categoryImageService;

    public CategoryImageController(CategoryImageService categoryImageService) {
        this.categoryImageService = categoryImageService;
    }

    @GetMapping("/categories/{categoryId}/image")
    public ResponseEntity<EntityImageInfoDto> getImage(@PathVariable String categoryId) {
        return ResponseEntity.ok(categoryImageService.getImageInfo(categoryId));
    }

    @PostMapping(value = "/categories/{categoryId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EntityImageInfoDto> uploadImage(@RequestHeader(value = "X-User", required = false) String xUser,
                                                          @PathVariable String categoryId,
                                                          @RequestPart("file") MultipartFile file) {
        String principal = xUser == null ? "system" : xUser;
        return ResponseEntity.ok(categoryImageService.uploadImage(categoryId, file, principal));
    }

    @GetMapping("/category-images/{categoryId}/{fileName}")
    public ResponseEntity<Resource> serveImage(@PathVariable String categoryId,
                                             @PathVariable String fileName) {
        Resource resource = categoryImageService.loadImageFile(categoryId, fileName);
        return ResponseEntity.ok()
                .contentType(ImageFileSupport.resolveMediaType(fileName))
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .body(resource);
    }
}
