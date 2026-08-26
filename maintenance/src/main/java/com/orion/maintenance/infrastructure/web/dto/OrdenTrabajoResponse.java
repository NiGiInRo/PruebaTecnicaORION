package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.EstadoOrdenTrabajo;
import com.orion.maintenance.domain.model.OrdenTrabajo;
import com.orion.maintenance.domain.model.OrigenOrdenTrabajo;
import com.orion.maintenance.domain.model.PrioridadOrdenTrabajo;
import com.orion.maintenance.domain.model.TipoOrdenTrabajo;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OrdenTrabajoResponse(
        Long id,
        ActivoResponse activo,
        TipoOrdenTrabajo tipo,
        PrioridadOrdenTrabajo prioridad,
        EstadoOrdenTrabajo estado,
        String descripcion,
        Instant fechaCreacion,
        LocalDate fechaProgramada,
        Instant fechaInicioEjecucion,
        Instant fechaCierre,
        String observacionesCierre,
        OrigenOrdenTrabajo origen,
        UsuarioResumenResponse creadoPor,
        List<OrdenTrabajoCuadrillaResponse> cuadrillasAsignadas) {

    public static OrdenTrabajoResponse from(OrdenTrabajo ot) {
        return new OrdenTrabajoResponse(
                ot.getId(),
                ActivoResponse.from(ot.getActivo()),
                ot.getTipo(),
                ot.getPrioridad(),
                ot.getEstado(),
                ot.getDescripcion(),
                ot.getFechaCreacion(),
                ot.getFechaProgramada(),
                ot.getFechaInicioEjecucion(),
                ot.getFechaCierre(),
                ot.getObservacionesCierre(),
                ot.getOrigen(),
                ot.getCreadoPor() == null ? null : UsuarioResumenResponse.from(ot.getCreadoPor()),
                ot.getCuadrillasAsignadas().stream().map(OrdenTrabajoCuadrillaResponse::from).toList());
    }
}
