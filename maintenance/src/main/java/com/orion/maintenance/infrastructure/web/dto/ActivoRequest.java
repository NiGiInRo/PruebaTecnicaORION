package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.TipoActivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ActivoRequest(
        @NotBlank @Size(max = 30) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @NotNull TipoActivo tipo,
        @NotNull Long corredorId,
        BigDecimal pkKilometraje,
        String fabricante,
        String modelo,
        LocalDate fechaInstalacion) {}
