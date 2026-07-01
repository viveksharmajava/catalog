package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.EntityImageInfoDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface CatalogImageService {

    EntityImageInfoDto getImageInfo(String prodCatalogId);

    EntityImageInfoDto uploadImage(String prodCatalogId, MultipartFile file, String principal);

    Resource loadImageFile(String prodCatalogId, String fileName);
}
