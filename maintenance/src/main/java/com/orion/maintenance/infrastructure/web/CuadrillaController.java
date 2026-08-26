package com.orion.maintenance.infrastructure.web;

import com.orion.maintenance.application.service.CuadrillaService;
import com.orion.maintenance.infrastructure.web.dto.CuadrillaRequest;
import com.orion.maintenance.infrastructure.web.dto.CuadrillaResponse;
import com.orion.maintenance.infrastructure.web.dto.UsuarioIdRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/{id}/tecnicos")
    @PreAuthorize("hasRole('COORDINADOR')")
    public CuadrillaResponse agregarTecnico(
            @PathVariable Long id, @Valid @RequestBody UsuarioIdRequest request) {
        return CuadrillaResponse.from(cuadrillaService.agregarTecnico(id, request.usuarioId()));
    }

    @DeleteMapping("/{id}/tecnicos/{usuarioId}")
    @PreAuthorize("hasRole('COORDINADOR')")
    public CuadrillaResponse quitarTecnico(@PathVariable Long id, @PathVariable Long usuarioId) {
        return CuadrillaResponse.from(cuadrillaService.quitarTecnico(id, usuarioId));
    }

    @PostMapping("/{id}/lider")
    @PreAuthorize("hasRole('COORDINADOR')")
    public CuadrillaResponse designarLider(
            @PathVariable Long id, @Valid @RequestBody UsuarioIdRequest request) {
        return CuadrillaResponse.from(cuadrillaService.designarLider(id, request.usuarioId()));
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasRole('COORDINADOR')")
    public CuadrillaResponse activar(@PathVariable Long id) {
        return CuadrillaResponse.from(cuadrillaService.activar(id));
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('COORDINADOR')")
    public CuadrillaResponse desactivar(@PathVariable Long id) {
        return CuadrillaResponse.from(cuadrillaService.desactivar(id));
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
