package com.playpro.playpro.catalog.service;

import com.playpro.playpro.catalog.dto.LoginRequest;
import com.playpro.playpro.catalog.dto.LoginResponse;
import com.playpro.playpro.catalog.entity.admin.UserAccount;
import com.playpro.playpro.catalog.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        UserAccount user = userAccountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String roleName = user.getRole() != null ? user.getRole().getName() : "VIEWER";
        String authHeader = user.getUsername() + ":" + roleName;
        return new LoginResponse(user.getUsername(), Collections.singletonList(roleName), authHeader);
    }
}
