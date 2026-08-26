package com.orion.maintenance.infrastructure.web;

import com.orion.maintenance.application.service.AsignacionCuadrillaInput;
import com.orion.maintenance.application.service.OrdenTrabajoService;
import com.orion.maintenance.domain.model.EstadoOrdenTrabajo;
import com.orion.maintenance.domain.model.PrioridadOrdenTrabajo;
import com.orion.maintenance.domain.model.TipoOrdenTrabajo;
import com.orion.maintenance.infrastructure.web.dto.AsignarCuadrillasRequest;
import com.orion.maintenance.infrastructure.web.dto.CancelarOrdenTrabajoRequest;
import com.orion.maintenance.infrastructure.web.dto.CerrarOrdenTrabajoRequest;
import com.orion.maintenance.infrastructure.web.dto.OrdenTrabajoRequest;
import com.orion.maintenance.infrastructure.web.dto.OrdenTrabajoResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ordenes-trabajo")
@RequiredArgsConstructor
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenTrabajoService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERVISOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenTrabajoResponse crear(@Valid @RequestBody OrdenTrabajoRequest request, Principal principal) {
        return OrdenTrabajoResponse.from(
                ordenTrabajoService.crear(
                        request.activoId(),
                        request.tipo(),
                        request.prioridad(),
                        request.descripcion(),
                        request.fechaProgramada(),
                        principal.getName()));
    }

    @PostMapping("/{id}/asignar-cuadrillas")
    @PreAuthorize("hasRole('COORDINADOR')")
    public OrdenTrabajoResponse asignarCuadrillas(
            @PathVariable Long id, @Valid @RequestBody AsignarCuadrillasRequest request) {
        List<AsignacionCuadrillaInput> asignaciones =
                request.asignaciones().stream()
                        .map(a -> new AsignacionCuadrillaInput(a.cuadrillaId(), a.rol()))
                        .toList();
        return OrdenTrabajoResponse.from(ordenTrabajoService.asignarCuadrillas(id, asignaciones));
    }

    @PostMapping("/{id}/iniciar-ejecucion")
    @PreAuthorize("hasRole('TECNICO')")
    public OrdenTrabajoResponse iniciarEjecucion(@PathVariable Long id) {
        return OrdenTrabajoResponse.from(ordenTrabajoService.iniciarEjecucion(id));
    }

    @PostMapping("/{id}/cerrar")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public OrdenTrabajoResponse cerrar(
            @PathVariable Long id, @Valid @RequestBody CerrarOrdenTrabajoRequest request) {
        return OrdenTrabajoResponse.from(ordenTrabajoService.cerrar(id, request.observaciones()));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public OrdenTrabajoResponse cancelar(
            @PathVariable Long id, @Valid @RequestBody CancelarOrdenTrabajoRequest request) {
        return OrdenTrabajoResponse.from(ordenTrabajoService.cancelar(id, request.motivo()));
    }

    @GetMapping
    public List<OrdenTrabajoResponse> listar(
            @RequestParam(required = false) Long activoId,
            @RequestParam(required = false) EstadoOrdenTrabajo estado,
            @RequestParam(required = false) TipoOrdenTrabajo tipo,
            @RequestParam(required = false) PrioridadOrdenTrabajo prioridad) {
        return ordenTrabajoService.listar(activoId, estado, tipo, prioridad).stream()
                .map(OrdenTrabajoResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public OrdenTrabajoResponse obtener(@PathVariable Long id) {
        return OrdenTrabajoResponse.from(ordenTrabajoService.obtenerPorId(id));
    }
}
