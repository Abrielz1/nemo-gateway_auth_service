package com.nemo.gateway_auth_service.web.controller.login;

import com.google.protobuf.Empty;
import com.nemo.auth.grpc.ClientAuthResponse;
import com.nemo.auth.grpc.ClientLoginRequest;
import com.nemo.auth.grpc.ClientLoginServiceGrpc;
import com.nemo.auth.grpc.ClientLogoutRequest;
import com.nemo.auth.grpc.ClientRefreshRequest;
import com.nemo.gateway_auth_service.app.service.orchestration.facade.ClientLoginFacade;
import com.nemo.gateway_auth_service.app.util.mapper.client.to.ClientTo;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ClientLoginController extends ClientLoginServiceGrpc.ClientLoginServiceImplBase {

    private final ClientTo clientTo;

    private final ClientLoginFacade clientLoginFacade;

    @Override
    public void login(ClientLoginRequest request, StreamObserver<ClientAuthResponse> responseObserver) {

        var loginRequest = this.clientTo.toLoginRequest(request);
        var tokenDto = this.clientLoginFacade.login(loginRequest);

       responseObserver.onNext(request);
       responseObserver.onCompleted();
    }

    @Override
    public void refresh(ClientRefreshRequest request, StreamObserver<ClientAuthResponse> responseObserver) {
        super.refresh(request, responseObserver);
    }

    @Override
    public void logout(ClientLogoutRequest request, StreamObserver<Empty> responseObserver) {
        super.logout(request, responseObserver);
    }
}
