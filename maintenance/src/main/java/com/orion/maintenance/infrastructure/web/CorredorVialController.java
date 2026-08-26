package com.orion.maintenance.infrastructure.web;

import com.orion.maintenance.application.service.CorredorVialService;
import com.orion.maintenance.infrastructure.web.dto.CorredorVialRequest;
import com.orion.maintenance.infrastructure.web.dto.CorredorVialResponse;
import com.orion.maintenance.infrastructure.web.dto.CorredorVialUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/corredores")
@RequiredArgsConstructor
public class CorredorVialController {

    private final CorredorVialService corredorVialService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERVISOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public CorredorVialResponse crear(@Valid @RequestBody CorredorVialRequest request) {
        return CorredorVialResponse.from(
                corredorVialService.crear(request.codigo(), request.nombre(), request.descripcion()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public CorredorVialResponse actualizar(
            @PathVariable Long id, @Valid @RequestBody CorredorVialUpdateRequest request) {
        return CorredorVialResponse.from(
                corredorVialService.actualizar(id, request.nombre(), request.descripcion()));
    }

    @GetMapping
    public List<CorredorVialResponse> listar() {
        return corredorVialService.listar().stream().map(CorredorVialResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CorredorVialResponse obtener(@PathVariable Long id) {
        return CorredorVialResponse.from(corredorVialService.obtenerPorId(id));
    }
}
