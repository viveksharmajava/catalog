package com.playpro.playpro.catalog.service.impl;

import com.playpro.playpro.catalog.dto.ProdCatalogDto;
import com.playpro.playpro.catalog.dto.ProdCatalogFindRequest;
import com.playpro.playpro.catalog.dto.ProdCatalogFindResponse;
import com.playpro.playpro.catalog.entity.catalog.ProdCatalog;
import com.playpro.playpro.catalog.exception.ResourceNotFoundException;
import com.playpro.playpro.catalog.helper.ProductSearchSpecifications;
import com.playpro.playpro.catalog.mapper.ProdCatalogMapper;
import com.playpro.playpro.catalog.repository.ProdCatalogRepository;
import com.playpro.playpro.catalog.service.ProdCatalogService;
import com.playpro.playpro.catalog.util.ProductIdGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProdCatalogServiceImpl implements ProdCatalogService {

    private static final Set<String> SORTABLE_FIELDS = new HashSet<>(Arrays.asList(
            "prodCatalogId", "catalogName", "useQuickAdd"
    ));

    private final ProdCatalogRepository prodCatalogRepository;

    public ProdCatalogServiceImpl(ProdCatalogRepository prodCatalogRepository) {
        this.prodCatalogRepository = prodCatalogRepository;
    }

    @Override
    public ProdCatalogDto createCatalog(ProdCatalogDto dto) {
        if (!StringUtils.hasText(dto.getCatalogName())) {
            throw new IllegalArgumentException("Catalog name is required");
        }

        ProdCatalog entity = new ProdCatalog();
        String catalogId = StringUtils.hasText(dto.getProdCatalogId())
                ? dto.getProdCatalogId().trim()
                : ProductIdGenerator.nextProdCatalogId();
        if (prodCatalogRepository.existsById(catalogId)) {
            throw new IllegalArgumentException("Product catalog already exists: " + catalogId);
        }
        entity.setProdCatalogId(catalogId);
        ProdCatalogMapper.applyDtoToEntity(dto, entity);
        return ProdCatalogMapper.toDto(prodCatalogRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public ProdCatalogDto getCatalog(String prodCatalogId) {
        ProdCatalog entity = prodCatalogRepository.findById(prodCatalogId)
                .orElseThrow(() -> new ResourceNotFoundException("Product catalog not found: " + prodCatalogId));
        return ProdCatalogMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public ProdCatalogFindResponse findCatalogs(ProdCatalogFindRequest request) {
        if (!request.hasFieldConditions() && !request.isNoConditionFind()) {
            ProdCatalogFindResponse empty = new ProdCatalogFindResponse();
            empty.setPage(Math.max(request.getPage(), 0));
            empty.setSize(Math.max(request.getSize(), 1));
            return empty;
        }

        Specification<ProdCatalog> spec = ProductSearchSpecifications.combineAll(
                ProductSearchSpecifications.fieldCriteria("prodCatalogId", request.getProdCatalogId()),
                ProductSearchSpecifications.fieldCriteria("catalogName", request.getCatalogName())
        );

        int page = Math.max(request.getPage(), 0);
        int size = Math.min(Math.max(request.getSize(), 1), 100);
        Sort sort = buildSort(request.getSortField(), request.getSortDirection());
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProdCatalog> result = spec == null
                ? prodCatalogRepository.findAll(pageable)
                : prodCatalogRepository.findAll(spec, pageable);

        ProdCatalogFindResponse response = new ProdCatalogFindResponse();
        response.setPage(result.getNumber());
        response.setSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());
        response.setContent(result.getContent().stream()
                .map(ProdCatalogMapper::toSummaryDto)
                .collect(Collectors.toList()));
        return response;
    }

    private Sort buildSort(String sortField, String sortDirection) {
        String field = SORTABLE_FIELDS.contains(sortField) ? sortField : "catalogName";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}
