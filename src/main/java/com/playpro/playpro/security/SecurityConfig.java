package com.playpro.playpro.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${catalog.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public StubAuthFilter stubAuthFilter() {
        return new StubAuthFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .headers().frameOptions().sameOrigin().and()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new JsonUnauthorizedEntryPoint())
                .and()
                .addFilterBefore(stubAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/catalog/auth/login").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers(HttpMethod.POST, "/catalog/products/find").hasAnyAuthority("ADMIN", "CATALOG_MANAGER", "MERCHANDISER", "VIEWER")
                .antMatchers(HttpMethod.POST, "/catalog/prod-catalogs/find").hasAnyAuthority("ADMIN", "CATALOG_MANAGER", "MERCHANDISER", "VIEWER")
                .antMatchers(HttpMethod.POST, "/catalog/categories/find").hasAnyAuthority("ADMIN", "CATALOG_MANAGER", "MERCHANDISER", "VIEWER")
                .antMatchers(HttpMethod.GET, "/catalog/products/**").hasAnyAuthority("ADMIN", "CATALOG_MANAGER", "MERCHANDISER", "VIEWER")
                .antMatchers(HttpMethod.GET, "/catalog/prod-catalogs/**").hasAnyAuthority("ADMIN", "CATALOG_MANAGER", "MERCHANDISER", "VIEWER")
                .antMatchers(HttpMethod.GET, "/catalog/product-stores/**").hasAnyAuthority("ADMIN", "CATALOG_MANAGER", "MERCHANDISER", "VIEWER")
                .antMatchers(HttpMethod.GET, "/catalog/categories/**").hasAnyAuthority("ADMIN", "CATALOG_MANAGER", "MERCHANDISER", "VIEWER")
                .antMatchers(HttpMethod.GET, "/catalog/reference/**").hasAnyAuthority("ADMIN", "CATALOG_MANAGER", "MERCHANDISER", "VIEWER")
                .antMatchers(HttpMethod.GET, "/catalog/product-images/**").permitAll()
                .antMatchers(HttpMethod.GET, "/catalog/category-images/**").permitAll()
                .antMatchers(HttpMethod.GET, "/catalog/catalog-images/**").permitAll()
                .antMatchers(HttpMethod.GET, "/catalog/storefront/**").permitAll()
                .antMatchers("/catalog/**").hasAnyAuthority("ADMIN", "CATALOG_MANAGER", "MERCHANDISER")
                .anyRequest().denyAll();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("X-User"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
