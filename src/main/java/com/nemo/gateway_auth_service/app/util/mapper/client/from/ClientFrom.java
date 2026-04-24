package com.nemo.gateway_auth_service.app.util.mapper.client.from;

import com.nemo.auth.grpc.ClientAuthResponse;
import com.nemo.gateway_auth_service.web.model.response.ClientRegistrationResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientFrom {

    public ClientAuthResponse toGrpcResponse(ClientRegistrationResponseDto clientRegistrationResponseDto) {

        return ClientAuthResponse.newBuilder()
                .setAccessToken(clientRegistrationResponseDto.accessToken())
                .setRefreshToken(clientRegistrationResponseDto.refreshToken())
                .build();
    }
}
