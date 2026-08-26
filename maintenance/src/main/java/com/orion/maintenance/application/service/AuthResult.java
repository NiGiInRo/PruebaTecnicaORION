package com.orion.maintenance.application.service;

import com.orion.maintenance.domain.model.Rol;

public record AuthResult(String token, String nombre, String email, Rol rol) {}
