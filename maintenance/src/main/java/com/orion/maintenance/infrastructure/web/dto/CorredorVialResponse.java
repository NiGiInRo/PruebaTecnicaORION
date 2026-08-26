package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.CorredorVial;

public record CorredorVialResponse(Long id, String codigo, String nombre, String descripcion) {

    public static CorredorVialResponse from(CorredorVial corredor) {
        return new CorredorVialResponse(
                corredor.getId(), corredor.getCodigo(), corredor.getNombre(), corredor.getDescripcion());
    }
}
