package com.orion.maintenance.application.service;

import com.orion.maintenance.application.exception.RecursoNoEncontradoException;
import com.orion.maintenance.application.port.MovimientoInventarioRepository;
import com.orion.maintenance.application.port.OrdenTrabajoRepository;
import com.orion.maintenance.application.port.UsuarioRepository;
import com.orion.maintenance.domain.model.Material;
import com.orion.maintenance.domain.model.MovimientoInventario;
import com.orion.maintenance.domain.model.OrdenTrabajo;
import com.orion.maintenance.domain.model.TipoMovimientoInventario;
import com.orion.maintenance.domain.model.Usuario;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventarioService {

    private final MaterialService materialService;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimientoInventario registrarEntrada(Long materialId, BigDecimal cantidad, String usuarioEmail) {
        Material material = materialService.obtenerPorId(materialId);
        Usuario usuario = obtenerUsuario(usuarioEmail);

        material.registrarEntrada(cantidad);

        return movimientoInventarioRepository.save(
                new MovimientoInventario(material, null, TipoMovimientoInventario.ENTRADA, cantidad, usuario));
    }

    public MovimientoInventario registrarConsumo(
            Long materialId, Long otId, BigDecimal cantidad, String usuarioEmail) {
        Material material = materialService.obtenerPorId(materialId);
        OrdenTrabajo ot =
                ordenTrabajoRepository
                        .findById(otId)
                        .orElseThrow(
                                () -> new RecursoNoEncontradoException("Orden de trabajo no encontrada: " + otId));
        Usuario usuario = obtenerUsuario(usuarioEmail);

        material.registrarSalida(cantidad);

        return movimientoInventarioRepository.save(
                new MovimientoInventario(material, ot, TipoMovimientoInventario.SALIDA, cantidad, usuario));
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventario> listarMovimientos(Long materialId) {
        return movimientoInventarioRepository.findByMaterialIdOrderByFechaDesc(materialId);
    }

    private Usuario obtenerUsuario(String email) {
        return usuarioRepository
                .findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + email));
    }
}
