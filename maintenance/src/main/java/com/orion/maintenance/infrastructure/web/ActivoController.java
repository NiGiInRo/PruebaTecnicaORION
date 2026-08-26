package com.orion.maintenance.infrastructure.web;

import com.orion.maintenance.application.service.ActivoService;
import com.orion.maintenance.domain.model.EstadoActivo;
import com.orion.maintenance.domain.model.TipoActivo;
import com.orion.maintenance.infrastructure.web.dto.ActivoRequest;
import com.orion.maintenance.infrastructure.web.dto.ActivoResponse;
import com.orion.maintenance.infrastructure.web.dto.ActivoUpdateRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activos")
@RequiredArgsConstructor
public class ActivoController {

    private final ActivoService activoService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERVISOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public ActivoResponse crear(@Valid @RequestBody ActivoRequest request) {
        return ActivoResponse.from(
                activoService.crear(
                        request.codigo(),
                        request.nombre(),
                        request.tipo(),
                        request.corredorId(),
                        request.pkKilometraje(),
                        request.fabricante(),
                        request.modelo(),
                        request.fechaInstalacion()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ActivoResponse actualizar(
            @PathVariable Long id, @Valid @RequestBody ActivoUpdateRequest request) {
        return ActivoResponse.from(
                activoService.actualizar(
                        id,
                        request.nombre(),
                        request.tipo(),
                        request.corredorId(),
                        request.pkKilometraje(),
                        request.fabricante(),
                        request.modelo(),
                        request.fechaInstalacion()));
    }

    @GetMapping
    public List<ActivoResponse> listar(
            @RequestParam(required = false) Long corredorId,
            @RequestParam(required = false) TipoActivo tipo,
            @RequestParam(required = false) EstadoActivo estado) {
        return activoService.listar(corredorId, tipo, estado).stream()
                .map(ActivoResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ActivoResponse obtener(@PathVariable Long id) {
        return ActivoResponse.from(activoService.obtenerPorId(id));
    }
}
