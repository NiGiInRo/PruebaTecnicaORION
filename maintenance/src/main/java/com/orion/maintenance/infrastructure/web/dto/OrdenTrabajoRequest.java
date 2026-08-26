package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.PrioridadOrdenTrabajo;
import com.orion.maintenance.domain.model.TipoOrdenTrabajo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record OrdenTrabajoRequest(
        @NotNull Long activoId,
        @NotNull TipoOrdenTrabajo tipo,
        @NotNull PrioridadOrdenTrabajo prioridad,
        @Size(max = 1000) String descripcion,
        LocalDate fechaProgramada) {}
