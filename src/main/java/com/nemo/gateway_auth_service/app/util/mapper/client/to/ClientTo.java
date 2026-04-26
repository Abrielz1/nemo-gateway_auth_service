package com.nemo.gateway_auth_service.app.util.mapper.client.to;

import com.nemo.auth.grpc.ClientLoginRequest;
import com.nemo.auth.grpc.ClientRegisterRequest;
import com.nemo.gateway_auth_service.app.domain.entity.child.Client;
import com.nemo.gateway_auth_service.app.domain.entity.enums.RoleType;
import com.nemo.gateway_auth_service.app.domain.entity.parent.EmailData;
import com.nemo.gateway_auth_service.app.domain.entity.parent.LoginData;
import com.nemo.gateway_auth_service.app.domain.entity.parent.PasswordData;
import com.nemo.gateway_auth_service.app.domain.entity.parent.PhoneData;
import com.nemo.gateway_auth_service.web.model.request.ClientLoginRequestDTO;
import com.nemo.gateway_auth_service.web.model.request.ClientRegistrationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ClientTo {

    private final PasswordEncoder passwordEncoder;

    public Client toEntity(ClientRegistrationRequestDTO clientRegistrationRequestDTO) {

        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(clientRegistrationRequestDTO.dateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты рождения. Ожидается YYYY-MM-DD", e);
        }

        Client client = Client.builder()
                .dateOfBirth(dateOfBirth)
                .enabled(true)
                .isDeleted(false)
                .isBanned(false)
                .roles(Set.of(RoleType.ROLE_CLIENT))
                .build();

        LoginData loginData = LoginData.builder()
                .login(clientRegistrationRequestDTO.username())
                .build();

        client.addLoginData(loginData);

        PasswordData passwordData = PasswordData.builder()
                .password(this.passwordEncoder.encode(clientRegistrationRequestDTO.password()))
                .timeWhenSet(Instant.now())
                .timeToLive(Instant.now().plusSeconds(Duration.ofDays(180).toSeconds()))
                .isActive(true)
                .build();

        client.addPasswordData(passwordData);

        PhoneData phoneData = PhoneData.builder()
                .phone(clientRegistrationRequestDTO.phone())
                .build();

        client.addPhoneData(phoneData);

        EmailData emailData = EmailData.builder()
                .email(clientRegistrationRequestDTO.email())
                .build();

        client.addEmailData(emailData);

        return client;
    }

    public ClientRegistrationRequestDTO toRegisterRequest(ClientRegisterRequest clientRegisterRequest) {

        return ClientRegistrationRequestDTO.builder()
                .username(clientRegisterRequest.getUsername())
                .email(clientRegisterRequest.getEmail())
                .phone(clientRegisterRequest.getPhone())
                .password(clientRegisterRequest.getPassword())
                .dateOfBirth(clientRegisterRequest.getDateOfBirth())
                .build();
    }

    public ClientLoginRequestDTO toLoginRequest(ClientLoginRequest request) {

        return ClientLoginRequestDTO.builder()
                .clientIdentifier(request.getIdentifier())
                .password(request.getPassword())
                .build();
    }
}
