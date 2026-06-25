package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.client.PricingServiceClient;
import com.playpro.playpro.catalog.client.dto.ProductPriceClientDto;
import com.playpro.playpro.catalog.dto.ProductAttributeDto;
import com.playpro.playpro.catalog.dto.ProductCategoryDto;
import com.playpro.playpro.catalog.dto.ProductDto;
import com.playpro.playpro.catalog.dto.ProductImportResultDto;
import com.playpro.playpro.catalog.dto.ProductImportRowErrorDto;
import com.playpro.playpro.catalog.entity.product.GoodIdentification;
import com.playpro.playpro.catalog.entity.product.Product;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.helper.ProductWorker;
import com.playpro.playpro.catalog.importexport.ProductImportRow;
import com.playpro.playpro.catalog.importexport.ProductSpreadsheetSupport;
import com.playpro.playpro.catalog.repository.GoodIdentificationRepository;
import com.playpro.playpro.catalog.repository.ProductRepository;
import com.playpro.playpro.catalog.service.ProductImportExportService;
import com.playpro.playpro.catalog.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductImportExportServiceImpl implements ProductImportExportService {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final GoodIdentificationRepository goodIdentificationRepository;
    private final PricingServiceClient pricingServiceClient;

    public ProductImportExportServiceImpl(ProductService productService,
                                          ProductRepository productRepository,
                                          GoodIdentificationRepository goodIdentificationRepository,
                                          PricingServiceClient pricingServiceClient) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.goodIdentificationRepository = goodIdentificationRepository;
        this.pricingServiceClient = pricingServiceClient;
    }

    @Override
    public byte[] generateTemplate() throws IOException {
        return ProductSpreadsheetSupport.writeWorkbook(new ArrayList<>(), true);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportProducts(String xUser) throws IOException {
        List<Map<String, String>> rows = new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            ProductDto dto = productService.getProduct(product.getProductId());
            ProductImportRow row = new ProductImportRow();
            row.setProduct(dto);
            row.setCategoryIds(resolveCategoryIds(dto));
            row.setAttributes(dto.getAttributes() == null ? new ArrayList<>() : dto.getAttributes());
            List<ProductPriceClientDto> prices = pricingServiceClient.listPrices(product.getProductId(), xUser);
            rows.add(ProductSpreadsheetSupport.toCellMap(row, prices));
        }
        return ProductSpreadsheetSupport.writeWorkbook(rows, false);
    }

    @Override
    public ProductImportResultDto importProducts(MultipartFile file, String principal, String xUser) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Import file is required");
        }

        List<ProductImportRow> rows = ProductSpreadsheetSupport.readRows(file.getInputStream());
        if (rows.isEmpty()) {
            throw new IllegalArgumentException(
                    "No product data rows found. Keep the header row unchanged and enter products from row 2. "
                            + "Each row needs at least product_name (and sku or product_id for updates).");
        }
        ProductImportResultDto result = new ProductImportResultDto();
        result.setTotalRows(rows.size());

        for (ProductImportRow row : rows) {
            try {
                boolean created = importRow(row, principal, xUser);
                if (created) {
                    result.setCreated(result.getCreated() + 1);
                } else {
                    result.setUpdated(result.getUpdated() + 1);
                }
            } catch (Exception ex) {
                result.setFailed(result.getFailed() + 1);
                result.getErrors().add(new ProductImportRowErrorDto(
                        row.getRowNumber(),
                        row.getProduct().getProductId(),
                        row.getProduct().getSku(),
                        ex.getMessage() == null ? "Import failed" : ex.getMessage()));
            }
        }
        return result;
    }

    private boolean importRow(ProductImportRow row, String principal, String xUser) {
        ProductDto product = row.getProduct();
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("product_name is required");
        }

        Optional<String> existingProductId = resolveExistingProductId(product);
        ProductDto saved;
        boolean created;

        if (existingProductId.isPresent()) {
            String productId = existingProductId.get();
            row.applyImagePaths(productId);
            saved = productService.updateProduct(productId, product, principal);
            created = false;
        } else {
            saved = productService.createProduct(product, principal);
            row.applyImagePaths(saved.getProductId());
            saved = productService.updateProduct(saved.getProductId(), row.getProduct(), principal);
            created = true;
        }

        String productId = saved.getProductId();
        linkCategories(productId, row.getCategoryIds(), principal);
        importAttributes(productId, row.getAttributes(), principal);
        importPrices(productId, row.getPrices(), xUser);
        return created;
    }

    private Optional<String> resolveExistingProductId(ProductDto incoming) {
        if (incoming.getProductId() != null && !incoming.getProductId().trim().isEmpty()) {
            String productId = incoming.getProductId().trim();
            if (productRepository.existsById(productId)) {
                return Optional.of(productId);
            }
        }
        if (incoming.getSku() != null && !incoming.getSku().trim().isEmpty()) {
            return goodIdentificationRepository
                    .findByIdGoodIdentificationTypeIdAndIdValue(ProductWorker.ID_TYPE_SKU, incoming.getSku().trim())
                    .map(gid -> gid.getId().getProductId());
        }
        return Optional.empty();
    }

    private void linkCategories(String productId, List<String> categoryIds, String principal) {
        if (categoryIds == null || categoryIds.size() <= 1) {
            return;
        }
        String primaryCategoryId = categoryIds.get(0);
        for (int i = 1; i < categoryIds.size(); i++) {
            String categoryId = categoryIds.get(i);
            if (categoryId == null || categoryId.trim().isEmpty()) {
                continue;
            }
            if (primaryCategoryId != null && primaryCategoryId.equals(categoryId)) {
                continue;
            }
            productService.addCategoryToProduct(productId, categoryId.trim(), principal);
        }
    }

    private void importAttributes(String productId, List<ProductAttributeDto> attributes, String principal) {
        if (attributes == null) {
            return;
        }
        for (ProductAttributeDto attribute : attributes) {
            if (attribute.getAttrName() == null || attribute.getAttrName().trim().isEmpty()) {
                continue;
            }
            productService.addAttribute(productId, attribute, principal);
        }
    }

    private void importPrices(String productId, List<ProductPriceClientDto> prices, String xUser) {
        if (prices == null || prices.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (ProductPriceClientDto price : prices) {
            if (price.getPrice() == null) {
                continue;
            }
            price.setProductId(productId);
            if (price.getFromDate() == null) {
                price.setFromDate(now);
            }
            pricingServiceClient.createPrice(productId, price, xUser);
        }
    }

    private List<String> resolveCategoryIds(ProductDto dto) {
        List<String> ids = new ArrayList<>();
        if (dto.getPrimaryProductCategoryId() != null && !dto.getPrimaryProductCategoryId().trim().isEmpty()) {
            ids.add(dto.getPrimaryProductCategoryId().trim());
        }
        try {
            List<ProductCategoryDto> categories = productService.getCategoriesForProduct(dto.getProductId());
            for (ProductCategoryDto category : categories) {
                String categoryId = category.getProductCategoryId();
                if (categoryId != null && !ids.contains(categoryId)) {
                    ids.add(categoryId);
                }
            }
        } catch (ResourceNotFoundException ex) {
            return ids;
        }
        return ids;
    }
}
