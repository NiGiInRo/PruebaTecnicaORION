package com.orion.maintenance.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CorredorVialRequest(
        @NotBlank @Size(max = 30) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 500) String descripcion) {}
