package com.nemo.gateway_auth_service.app.repository;

import com.nemo.gateway_auth_service.app.domain.entity.child.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
}
