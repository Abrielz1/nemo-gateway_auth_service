package com.nemo.gateway_auth_service.web.controller.registration;

import com.google.protobuf.Empty;
import com.nemo.auth.grpc.EmployeeAuthResponse;
import com.nemo.auth.grpc.EmployeeLoginRequest;
import com.nemo.auth.grpc.EmployeeLoginServiceGrpc;
import com.nemo.auth.grpc.EmployeeLogoutRequest;
import com.nemo.auth.grpc.EmployeeRefreshRequest;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class EmployeeRegistrationController extends EmployeeLoginServiceGrpc.EmployeeLoginServiceImplBase {

    @Override
    public void login(EmployeeLoginRequest request, StreamObserver<EmployeeAuthResponse> responseObserver) {
        super.login(request, responseObserver);
    }

    @Override
    public void refresh(EmployeeRefreshRequest request, StreamObserver<EmployeeAuthResponse> responseObserver) {
        super.refresh(request, responseObserver);
    }

    @Override
    public void logout(EmployeeLogoutRequest request, StreamObserver<Empty> responseObserver) {
        super.logout(request, responseObserver);
    }
}
