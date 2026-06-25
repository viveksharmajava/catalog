package com.playpro.playpro.catalog.client;

import com.playpro.playpro.catalog.client.dto.ProductPriceClientDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class PricingServiceClient {

    private final RestTemplate restTemplate;
    private final String pricingBaseUrl;

    public PricingServiceClient(RestTemplate restTemplate,
                                @Value("${pricing.service.base-url:http://localhost:8081}") String pricingBaseUrl) {
        this.restTemplate = restTemplate;
        this.pricingBaseUrl = pricingBaseUrl;
    }

    public List<ProductPriceClientDto> listPrices(String productId, String xUser) {
        try {
            ResponseEntity<List<ProductPriceClientDto>> response = restTemplate.exchange(
                    pricingBaseUrl + "/pricing/products/" + productId + "/prices",
                    HttpMethod.GET,
                    new HttpEntity<>(headers(xUser)),
                    new ParameterizedTypeReference<List<ProductPriceClientDto>>() {
                    });
            return response.getBody() == null ? Collections.emptyList() : response.getBody();
        } catch (RestClientException ex) {
            return Collections.emptyList();
        }
    }

    public void createPrice(String productId, ProductPriceClientDto dto, String xUser) {
        restTemplate.postForEntity(
                pricingBaseUrl + "/pricing/products/" + productId + "/prices",
                new HttpEntity<>(dto, headers(xUser)),
                ProductPriceClientDto.class);
    }

    private HttpHeaders headers(String xUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (xUser != null && !xUser.trim().isEmpty()) {
            headers.set("X-User", xUser);
        }
        return headers;
    }
}
