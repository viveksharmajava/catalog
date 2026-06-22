package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductImageInfoDto;
import com.playpro.playpro.catalog.productimage.ProductImageSize;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {

    List<ProductImageInfoDto> listImages(String productId);

    ProductImageInfoDto uploadImage(String productId, ProductImageSize size, MultipartFile file, String principal);

    Resource loadImageFile(String productId, String fileName);
}
