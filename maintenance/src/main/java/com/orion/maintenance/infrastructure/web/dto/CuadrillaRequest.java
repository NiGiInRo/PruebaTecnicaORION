package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.EspecialidadCuadrilla;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CuadrillaRequest(
        @NotBlank @Size(max = 30) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @NotNull EspecialidadCuadrilla especialidad) {}
