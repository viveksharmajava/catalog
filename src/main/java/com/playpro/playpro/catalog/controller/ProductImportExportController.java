package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProductImportResultDto;
import com.playpro.playpro.catalog.service.ProductImportExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/catalog/products")
public class ProductImportExportController {

    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final ProductImportExportService productImportExportService;

    public ProductImportExportController(ProductImportExportService productImportExportService) {
        this.productImportExportService = productImportExportService;
    }

    @GetMapping("/import/template")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        byte[] content = productImportExportService.generateTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product_import_template.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(content);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportProducts(@RequestHeader(value = "X-User", required = false) String xUser)
            throws IOException {
        byte[] content = productImportExportService.exportProducts(xUser);
        String filename = "products_export_" + LocalDateTime.now().format(FILE_TS) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(content);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductImportResultDto> importProducts(
            @RequestHeader(value = "X-User", required = false) String xUser,
            @RequestParam("file") MultipartFile file) throws IOException {
        String principal = xUser == null ? "system" : xUser.split(":", 2)[0];
        return ResponseEntity.ok(productImportExportService.importProducts(file, principal, xUser));
    }
}
