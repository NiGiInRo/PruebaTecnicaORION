package com.orion.maintenance.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelarOrdenTrabajoRequest(@NotBlank @Size(max = 1000) String motivo) {}
