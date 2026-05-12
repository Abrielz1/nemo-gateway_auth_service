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
import org.springframework.security.authentication.BadCredentialsException;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ClientLoginController extends ClientLoginServiceGrpc.ClientLoginServiceImplBase {

    private final ClientTo clientTo;

    private final ClientLoginFacade clientLoginFacade;

    @Override
    public void login(ClientLoginRequest request, StreamObserver<ClientAuthResponse> responseObserver) {

        var tokenDto = this.clientLoginFacade.login(this.clientTo.toLoginRequest(request));

        var responseDto = ClientAuthResponse.newBuilder()
                .setAccessToken(tokenDto.accessToken())
                .setRefreshToken(tokenDto.refreshToken())
                .setUserId(tokenDto.userId())
                .build();

       responseObserver.onNext(responseDto);
       responseObserver.onCompleted();
    }

    @Override
    public void refresh(ClientRefreshRequest request, StreamObserver<ClientAuthResponse> responseObserver) {

      var response = this.clientLoginFacade.refresh(request.getRefreshToken());
      var responseDto = ClientAuthResponse.newBuilder()
              .setAccessToken(response.accessToken())
              .setRefreshToken(response.refreshToken())
              .setUserId(response.userId())
              .build();

      responseObserver.onNext(responseDto);
      responseObserver.onCompleted();
    }

    @Override
    public void logout(ClientLogoutRequest request, StreamObserver<Empty> responseObserver) {
    try {
        this.clientLoginFacade.logout(
                this.clientTo.toLogOutRequest(request)
        );
    } catch (BadCredentialsException e) {
        log.warn("Logout warning: {}", e.getMessage());
    }

       responseObserver.onNext(Empty.getDefaultInstance());
       responseObserver.onCompleted();
    }
}
