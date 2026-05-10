package com.nemo.gateway_auth_service.app.service.orchestration.worker;

public interface ClientLogOutWorker {

    void logout(String refreshToken);
}
