package com.orion.maintenance.infrastructure.web;

import com.orion.maintenance.application.service.InventarioService;
import com.orion.maintenance.application.service.MaterialService;
import com.orion.maintenance.infrastructure.web.dto.MaterialRequest;
import com.orion.maintenance.infrastructure.web.dto.MaterialResponse;
import com.orion.maintenance.infrastructure.web.dto.MovimientoInventarioResponse;
import com.orion.maintenance.infrastructure.web.dto.RegistrarConsumoRequest;
import com.orion.maintenance.infrastructure.web.dto.RegistrarEntradaRequest;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/materiales")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;
    private final InventarioService inventarioService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERVISOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public MaterialResponse crear(@Valid @RequestBody MaterialRequest request) {
        return MaterialResponse.from(
                materialService.crear(
                        request.codigo(), request.nombre(), request.unidadMedida(), request.stockMinimo()));
    }

    @GetMapping
    public List<MaterialResponse> listar() {
        return materialService.listar().stream().map(MaterialResponse::from).toList();
    }

    @GetMapping("/{id}")
    public MaterialResponse obtener(@PathVariable Long id) {
        return MaterialResponse.from(materialService.obtenerPorId(id));
    }

    @GetMapping("/{id}/movimientos")
    public List<MovimientoInventarioResponse> movimientos(@PathVariable Long id) {
        return inventarioService.listarMovimientos(id).stream().map(MovimientoInventarioResponse::from).toList();
    }

    @PostMapping("/{id}/entradas")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public MovimientoInventarioResponse registrarEntrada(
            @PathVariable Long id, @Valid @RequestBody RegistrarEntradaRequest request, Principal principal) {
        return MovimientoInventarioResponse.from(
                inventarioService.registrarEntrada(id, request.cantidad(), principal.getName()));
    }

    @PostMapping("/{id}/consumos")
    @PreAuthorize("hasRole('TECNICO')")
    public MovimientoInventarioResponse registrarConsumo(
            @PathVariable Long id, @Valid @RequestBody RegistrarConsumoRequest request, Principal principal) {
        return MovimientoInventarioResponse.from(
                inventarioService.registrarConsumo(
                        id, request.ordenTrabajoId(), request.cantidad(), principal.getName()));
    }
}
