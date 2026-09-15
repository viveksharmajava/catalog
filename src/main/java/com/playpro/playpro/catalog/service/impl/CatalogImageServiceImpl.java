package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.EntityImageInfoDto;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalog;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.media.ImageFileSupport;
import com.playpro.playpro.catalog.repository.ProdCatalogRepository;
import com.playpro.playpro.catalog.service.CatalogImageService;
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
public class CatalogImageServiceImpl implements CatalogImageService {

    private static final String IMAGE_PREFIX = "logo.";

    private final ProdCatalogRepository prodCatalogRepository;
    private final ImageObjectStore imageObjectStore;

    public CatalogImageServiceImpl(ProdCatalogRepository prodCatalogRepository,
                                   ImageObjectStore imageObjectStore) {
        this.prodCatalogRepository = prodCatalogRepository;
        this.imageObjectStore = imageObjectStore;
    }

    @Override
    @Transactional(readOnly = true)
    public EntityImageInfoDto getImageInfo(String prodCatalogId) {
        ProdCatalog catalog = loadCatalog(prodCatalogId);
        String url = catalog.getHeaderLogo();
        boolean uploaded = StringUtils.hasText(url);
        String fileName = null;
        if (uploaded && imageObjectStore.servesViaCatalogApi()) {
            fileName = imageObjectStore.findFileName(ImageFolders.CATALOG, prodCatalogId, IMAGE_PREFIX).orElse(null);
        } else if (uploaded) {
            fileName = extractFileName(url);
        }
        return buildImageInfo(url, fileName, uploaded, prodCatalogId);
    }

    @Override
    public EntityImageInfoDto uploadImage(String prodCatalogId, MultipartFile file, String principal) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        ProdCatalog catalog = loadCatalog(prodCatalogId);
        String extension = ImageFileSupport.resolveExtension(file);
        String fileName = IMAGE_PREFIX + extension;
        byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read catalog image", ex);
        }

        StoredImageObject stored = imageObjectStore.store(
                ImageFolders.CATALOG,
                prodCatalogId,
                fileName,
                IMAGE_PREFIX,
                content,
                ImageFileSupport.resolveMediaType(fileName).toString());

        catalog.setHeaderLogo(stored.getPublicUrl());
        prodCatalogRepository.save(catalog);

        return buildImageInfo(stored.getPublicUrl(), stored.getFileName(), true, prodCatalogId);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadImageFile(String prodCatalogId, String fileName) {
        return imageObjectStore.load(ImageFolders.CATALOG, prodCatalogId, fileName);
    }

    private ProdCatalog loadCatalog(String prodCatalogId) {
        return prodCatalogRepository.findById(prodCatalogId)
                .orElseThrow(() -> new ResourceNotFoundException("Catalog not found: " + prodCatalogId));
    }

    private EntityImageInfoDto buildImageInfo(String url, String fileName, boolean uploaded, String prodCatalogId) {
        EntityImageInfoDto dto = new EntityImageInfoDto();
        dto.setUrl(url);
        dto.setFileName(fileName);
        dto.setUploaded(uploaded);
        if (uploaded && StringUtils.hasText(fileName)) {
            dto.setStoragePath(ImageFolders.CATALOG + "/"
                    + ImageFileSupport.sanitizeEntityId(prodCatalogId) + "/" + fileName);
        }
        return dto;
    }

    private static String extractFileName(String url) {
        int slash = url.lastIndexOf('/');
        return slash >= 0 && slash < url.length() - 1 ? url.substring(slash + 1) : null;
    }
}
