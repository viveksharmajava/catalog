package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.EntityImageInfoDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface CategoryImageService {

    EntityImageInfoDto getImageInfo(String categoryId);

    EntityImageInfoDto uploadImage(String categoryId, MultipartFile file, String principal);

    Resource loadImageFile(String categoryId, String fileName);
}
