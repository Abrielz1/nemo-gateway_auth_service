package com.nemo.gateway_auth_service.app.service.orchestration.worker.impl;

import com.nemo.gateway_auth_service.app.security.service.JwtTokenProvider;
import com.nemo.gateway_auth_service.app.service.orchestration.worker.ClientLogOutWorker;
import com.nemo.gateway_auth_service.app.service.orchestration.worker.ClientSessionWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ClientLogOutWorkerImpl  implements ClientLogOutWorker {

    private final ClientSessionWorker clientSessionWorker;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(String refreshToken) {
    var claims = this.jwtTokenProvider.getClaimsFromToken(refreshToken);
        var userId = UUID.fromString(claims.getSubject());
        this.clientSessionWorker.deleteSession(userId);
    }
}
