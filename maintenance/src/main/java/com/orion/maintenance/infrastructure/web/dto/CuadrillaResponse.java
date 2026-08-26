package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.Cuadrilla;
import com.orion.maintenance.domain.model.EspecialidadCuadrilla;
import com.orion.maintenance.domain.model.EstadoCuadrilla;
import java.util.List;

public record CuadrillaResponse(
        Long id,
        String codigo,
        String nombre,
        EspecialidadCuadrilla especialidad,
        EstadoCuadrilla estado,
        UsuarioResumenResponse lider,
        List<UsuarioResumenResponse> tecnicos) {

    public static CuadrillaResponse from(Cuadrilla cuadrilla) {
        return new CuadrillaResponse(
                cuadrilla.getId(),
                cuadrilla.getCodigo(),
                cuadrilla.getNombre(),
                cuadrilla.getEspecialidad(),
                cuadrilla.getEstado(),
                cuadrilla.getLider() == null ? null : UsuarioResumenResponse.from(cuadrilla.getLider()),
                cuadrilla.getTecnicos().stream()
                        .map(ct -> UsuarioResumenResponse.from(ct.getUsuario()))
                        .toList());
    }
}
