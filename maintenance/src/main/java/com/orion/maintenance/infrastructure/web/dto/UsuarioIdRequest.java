package com.orion.maintenance.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioIdRequest(@NotNull Long usuarioId) {}
