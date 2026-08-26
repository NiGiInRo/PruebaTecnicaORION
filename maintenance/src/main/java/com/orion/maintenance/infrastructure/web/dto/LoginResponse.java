package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.application.service.AuthResult;
import com.orion.maintenance.domain.model.Rol;

public record LoginResponse(String token, String nombre, String email, Rol rol) {

    public static LoginResponse from(AuthResult result) {
        return new LoginResponse(result.token(), result.nombre(), result.email(), result.rol());
    }
}
