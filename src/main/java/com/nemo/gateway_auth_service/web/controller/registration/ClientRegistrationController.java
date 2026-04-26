package com.nemo.gateway_auth_service.web.controller.registration;

import com.nemo.auth.grpc.ClientAuthResponse;
import com.nemo.auth.grpc.ClientRegisterRequest;
import com.nemo.gateway_auth_service.app.service.orchestration.facade.ClientRegistrationFacade;
import com.nemo.gateway_auth_service.app.util.mapper.client.from.ClientFrom;
import com.nemo.gateway_auth_service.app.util.mapper.client.to.ClientTo;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ClientRegistrationController extends com.nemo.auth.grpc.ClientRegistrationServiceGrpc.ClientRegistrationServiceImplBase {

    private final ClientRegistrationFacade clientRegistrationFacade;

    private final ClientTo clientTo;

    private final ClientFrom clientFrom;

    @Override
    public void register(ClientRegisterRequest request, StreamObserver<ClientAuthResponse> responseObserver) {
    try {
      var response = this.clientRegistrationFacade.register(this.clientTo.toRegisterRequest(request));

      var clientAuthResponse = this.clientFrom.toGrpcResponse(response);
      responseObserver.onNext(clientAuthResponse);
      responseObserver.onCompleted();
    } catch (Exception e) {
        responseObserver.onError(io.grpc.Status.INTERNAL.withDescription(e.getMessage())
                .asRuntimeException());
    }
  }
}
