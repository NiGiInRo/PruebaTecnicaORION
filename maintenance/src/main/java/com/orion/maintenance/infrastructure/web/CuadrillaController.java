package com.orion.maintenance.infrastructure.web;

import com.orion.maintenance.application.service.CuadrillaService;
import com.orion.maintenance.infrastructure.web.dto.CuadrillaRequest;
import com.orion.maintenance.infrastructure.web.dto.CuadrillaResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Versión mínima para desbloquear la asignación en HU-002. CRUD completo
 * (técnicos, disponibilidad derivada) llega con HU-003.
 */
@RestController
@RequestMapping("/cuadrillas")
@RequiredArgsConstructor
public class CuadrillaController {

    private final CuadrillaService cuadrillaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'COORDINADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public CuadrillaResponse crear(@Valid @RequestBody CuadrillaRequest request) {
        return CuadrillaResponse.from(
                cuadrillaService.crear(request.codigo(), request.nombre(), request.especialidad()));
    }

    @GetMapping
    public List<CuadrillaResponse> listar() {
        return cuadrillaService.listar().stream().map(CuadrillaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CuadrillaResponse obtener(@PathVariable Long id) {
        return CuadrillaResponse.from(cuadrillaService.obtenerPorId(id));
    }
}
