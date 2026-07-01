package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductImportResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CategoryImportExportService {

    byte[] generateTemplate() throws IOException;

    byte[] exportCategories() throws IOException;

    ProductImportResultDto importCategories(MultipartFile file, String principal) throws IOException;
}
