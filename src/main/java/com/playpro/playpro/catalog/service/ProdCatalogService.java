package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.ProdCatalogDto;
import com.playpro.playpro.catalog.dto.ProdCatalogFindRequest;
import com.playpro.playpro.catalog.dto.ProdCatalogFindResponse;

public interface ProdCatalogService {

    ProdCatalogDto createCatalog(ProdCatalogDto dto);

    ProdCatalogDto getCatalog(String prodCatalogId);

    ProdCatalogDto updateCatalog(String prodCatalogId, ProdCatalogDto dto);

    ProdCatalogFindResponse findCatalogs(ProdCatalogFindRequest request);
}
