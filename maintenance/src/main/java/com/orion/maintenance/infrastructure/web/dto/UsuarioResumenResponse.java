package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.Usuario;

public record UsuarioResumenResponse(Long id, String nombre, String email) {

    public static UsuarioResumenResponse from(Usuario usuario) {
        return new UsuarioResumenResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }
}
