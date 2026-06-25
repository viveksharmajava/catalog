package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.LoginRequest;
import com.playpro.playpro.catalog.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Delegates authentication to the party service, which validates user_login credentials
 * and resolves admin UI roles from security-group permissions.
 */
@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final String partyBaseUrl;

    public AuthService(RestTemplate restTemplate,
                       @Value("${party.service.base-url:http://localhost:8082}") String partyBaseUrl) {
        this.restTemplate = restTemplate;
        this.partyBaseUrl = partyBaseUrl;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                    partyBaseUrl + "/party/auth/login",
                    entity,
                    LoginResponse.class);

            LoginResponse body = response.getBody();
            if (body == null) {
                throw new IllegalArgumentException("Invalid username or password");
            }
            return body;
        } catch (HttpStatusCodeException ex) {
            throw new IllegalArgumentException("Invalid username or password");
        } catch (RestClientException ex) {
            throw new IllegalStateException("Party authentication service is unavailable. Start the party service on port 8082.");
        }
    }
}
