package com.orion.maintenance.application.port;

/** Puerto de aplicación: emitir un token de sesión. La implementación (JWT) vive en infrastructure. */
public interface TokenIssuer {

    String issue(String email, String rol);
}
