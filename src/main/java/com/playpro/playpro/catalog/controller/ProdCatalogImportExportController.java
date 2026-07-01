package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductImportResultDto;
import com.playpro.playpro.catalog.service.ProdCatalogImportExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/catalog/prod-catalogs")
public class ProdCatalogImportExportController {

    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final ProdCatalogImportExportService prodCatalogImportExportService;

    public ProdCatalogImportExportController(ProdCatalogImportExportService prodCatalogImportExportService) {
        this.prodCatalogImportExportService = prodCatalogImportExportService;
    }

    @GetMapping("/import/template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        byte[] content = prodCatalogImportExportService.generateTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prod_catalog_import_template.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(content);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCatalogs() throws IOException {
        byte[] content = prodCatalogImportExportService.exportCatalogs();
        String filename = "prod_catalogs_export_" + LocalDateTime.now().format(FILE_TS) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(content);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductImportResultDto> importCatalogs(@RequestParam("file") MultipartFile file)
            throws IOException {
        return ResponseEntity.ok(prodCatalogImportExportService.importCatalogs(file));
    }
}
