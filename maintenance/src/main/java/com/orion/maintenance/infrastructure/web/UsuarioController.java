package com.orion.maintenance.infrastructure.web;

import com.orion.maintenance.application.service.UsuarioService;
import com.orion.maintenance.domain.model.Rol;
import com.orion.maintenance.infrastructure.web.dto.UsuarioResumenResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResumenResponse> listar(@RequestParam(required = false) Rol rol) {
        return usuarioService.listar(rol).stream().map(UsuarioResumenResponse::from).toList();
    }
}
