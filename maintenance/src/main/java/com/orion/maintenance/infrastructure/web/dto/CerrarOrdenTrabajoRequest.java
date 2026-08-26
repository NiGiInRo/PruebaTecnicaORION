package com.orion.maintenance.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CerrarOrdenTrabajoRequest(@NotBlank @Size(max = 1000) String observaciones) {}
