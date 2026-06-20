package com.playpro.playpro.catalog.controller;

import com.playpro.playpro.catalog.dto.ProdCatalogDto;
import com.playpro.playpro.catalog.dto.ProdCatalogFindRequest;
import com.playpro.playpro.catalog.dto.ProdCatalogFindResponse;
import com.playpro.playpro.catalog.service.ProdCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog/prod-catalogs")
@Validated
public class ProdCatalogController {

    private final ProdCatalogService prodCatalogService;

    public ProdCatalogController(ProdCatalogService prodCatalogService) {
        this.prodCatalogService = prodCatalogService;
    }

    @PostMapping
    public ResponseEntity<ProdCatalogDto> createCatalog(@RequestBody ProdCatalogDto dto) {
        return ResponseEntity.ok(prodCatalogService.createCatalog(dto));
    }

    @GetMapping("/{prodCatalogId}")
    public ResponseEntity<ProdCatalogDto> getCatalog(@PathVariable String prodCatalogId) {
        return ResponseEntity.ok(prodCatalogService.getCatalog(prodCatalogId));
    }

    @PostMapping("/find")
    public ResponseEntity<ProdCatalogFindResponse> findCatalogs(@RequestBody ProdCatalogFindRequest request) {
        return ResponseEntity.ok(prodCatalogService.findCatalogs(request));
    }
}
