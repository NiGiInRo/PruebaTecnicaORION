package com.orion.maintenance.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RegistrarEntradaRequest(@NotNull @DecimalMin(value = "0.001") BigDecimal cantidad) {}
