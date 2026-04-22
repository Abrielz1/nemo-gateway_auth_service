package com.nemo.gateway_auth_service.app.security.configuration;

import io.grpc.Metadata;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableMethodSecurity(proxyTargetClass = true)
public class GrpcSecurityConfig {

    private static final Metadata.Key<String> USER_ID_KEY = Metadata.Key.of(
            "X-UserId", Metadata.ASCII_STRING_MARSHALLER
            );

    private static final Metadata.Key<String> USER_ROLES_KEY = Metadata.Key.of(
                  "X-User-Roles", Metadata.ASCII_STRING_MARSHALLER
            );

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {

        return (call, headers) -> {

            var userIdKey = headers.get(USER_ID_KEY);
            var rolesStrKey = headers.get(USER_ROLES_KEY);

            if (!StringUtils.hasText(userIdKey) || !StringUtils.hasText(rolesStrKey)) {
                log.warn("Missing Security Headers in gRPC Request! User get send away!");
                throw io.grpc.Status.UNAUTHENTICATED
                        .withDescription("Missing internal security headers")
                        .asRuntimeException();
            }

            List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesStrKey.split(","))
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                    .toList();
            log.info("user was proceed auth with succeed with ID {} and role {}", userIdKey, authorities);

            return new UsernamePasswordAuthenticationToken(userIdKey, null, authorities);
        };
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> authentication;
    }
}
