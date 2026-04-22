package com.nemo.gateway_auth_service.web.controller.registration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RegistrationController extends com.nemo.auth.grpc.RegistrationServiceGrpc.RegistrationServiceImplBase {



}
