package com.nemo.gateway_auth_service.web.controller.login;

import com.nemo.auth.grpc.LoginServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class LoginController extends LoginServiceGrpc.LoginServiceImplBase {



}
