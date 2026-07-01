package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProductImportResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProdCatalogImportExportService {

    byte[] generateTemplate() throws IOException;

    byte[] exportCatalogs() throws IOException;

    ProductImportResultDto importCatalogs(MultipartFile file) throws IOException;
}
