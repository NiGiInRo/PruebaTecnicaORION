package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.Cuadrilla;
import com.orion.maintenance.domain.model.EspecialidadCuadrilla;
import com.orion.maintenance.domain.model.EstadoCuadrilla;

public record CuadrillaResponse(
        Long id, String codigo, String nombre, EspecialidadCuadrilla especialidad, EstadoCuadrilla estado) {

    public static CuadrillaResponse from(Cuadrilla cuadrilla) {
        return new CuadrillaResponse(
                cuadrilla.getId(),
                cuadrilla.getCodigo(),
                cuadrilla.getNombre(),
                cuadrilla.getEspecialidad(),
                cuadrilla.getEstado());
    }
}
