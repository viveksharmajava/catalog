package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.EntityImageInfoDto;
import com.playpro.playpro.catalog.media.ImageFileSupport;
import com.playpro.playpro.catalog.service.CatalogImageService;
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
public class CatalogImageController {

    private final CatalogImageService catalogImageService;

    public CatalogImageController(CatalogImageService catalogImageService) {
        this.catalogImageService = catalogImageService;
    }

    @GetMapping("/prod-catalogs/{prodCatalogId}/image")
    public ResponseEntity<EntityImageInfoDto> getImage(@PathVariable String prodCatalogId) {
        return ResponseEntity.ok(catalogImageService.getImageInfo(prodCatalogId));
    }

    @PostMapping(value = "/prod-catalogs/{prodCatalogId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EntityImageInfoDto> uploadImage(@RequestHeader(value = "X-User", required = false) String xUser,
                                                          @PathVariable String prodCatalogId,
                                                          @RequestPart("file") MultipartFile file) {
        String principal = xUser == null ? "system" : xUser;
        return ResponseEntity.ok(catalogImageService.uploadImage(prodCatalogId, file, principal));
    }

    @GetMapping("/catalog-images/{prodCatalogId}/{fileName}")
    public ResponseEntity<Resource> serveImage(@PathVariable String prodCatalogId,
                                               @PathVariable String fileName) {
        Resource resource = catalogImageService.loadImageFile(prodCatalogId, fileName);
        return ResponseEntity.ok()
                .contentType(ImageFileSupport.resolveMediaType(fileName))
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .body(resource);
    }
}
