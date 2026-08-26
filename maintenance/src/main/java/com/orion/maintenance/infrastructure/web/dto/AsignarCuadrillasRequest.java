package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.RolCuadrillaEnOT;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AsignarCuadrillasRequest(@NotEmpty @Valid List<Asignacion> asignaciones) {

    public record Asignacion(@NotNull Long cuadrillaId, @NotNull RolCuadrillaEnOT rol) {}
}
