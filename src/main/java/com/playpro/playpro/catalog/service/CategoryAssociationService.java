package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.CategoryProdCatalogDto;
import com.playpro.playpro.catalog.dto.CategoryProductMemberDto;
import com.playpro.playpro.catalog.dto.CategoryRollupDto;

import java.util.List;

public interface CategoryAssociationService {

    List<CategoryRollupDto> listParentRollups(String categoryId);

    List<CategoryRollupDto> listChildRollups(String categoryId);

    CategoryRollupDto addParentRollup(String categoryId, CategoryRollupDto dto);

    CategoryRollupDto addChildRollup(String categoryId, CategoryRollupDto dto);

    CategoryRollupDto updateRollup(CategoryRollupDto dto);

    void removeRollup(CategoryRollupDto dto);

    List<CategoryProductMemberDto> listProductMembers(String categoryId);

    CategoryProductMemberDto addProductMember(String categoryId, CategoryProductMemberDto dto);

    CategoryProductMemberDto updateProductMember(CategoryProductMemberDto dto);

    void removeProductMember(CategoryProductMemberDto dto);

    List<CategoryProdCatalogDto> listProdCatalogs(String categoryId);

    CategoryProdCatalogDto addProdCatalog(String categoryId, CategoryProdCatalogDto dto);

    CategoryProdCatalogDto updateProdCatalog(CategoryProdCatalogDto dto);

    void removeProdCatalog(CategoryProdCatalogDto dto);
}
