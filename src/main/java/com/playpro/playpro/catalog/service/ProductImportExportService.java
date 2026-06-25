package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductImportResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductImportExportService {

    byte[] generateTemplate() throws IOException;

    byte[] exportProducts(String xUser) throws IOException;

    ProductImportResultDto importProducts(MultipartFile file, String principal, String xUser) throws IOException;
}
