package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.OrdenTrabajoCuadrilla;
import com.orion.maintenance.domain.model.RolCuadrillaEnOT;
import java.time.Instant;

public record OrdenTrabajoCuadrillaResponse(
        Long id, CuadrillaResponse cuadrilla, RolCuadrillaEnOT rol, Instant fechaAsignacion) {

    public static OrdenTrabajoCuadrillaResponse from(OrdenTrabajoCuadrilla asignacion) {
        return new OrdenTrabajoCuadrillaResponse(
                asignacion.getId(),
                CuadrillaResponse.from(asignacion.getCuadrilla()),
                asignacion.getRol(),
                asignacion.getFechaAsignacion());
    }
}
