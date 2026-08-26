package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.MovimientoInventario;
import com.orion.maintenance.domain.model.TipoMovimientoInventario;
import java.math.BigDecimal;
import java.time.Instant;

public record MovimientoInventarioResponse(
        Long id,
        MaterialResponse material,
        Long ordenTrabajoId,
        TipoMovimientoInventario tipo,
        BigDecimal cantidad,
        UsuarioResumenResponse usuario,
        Instant fecha) {

    public static MovimientoInventarioResponse from(MovimientoInventario mov) {
        return new MovimientoInventarioResponse(
                mov.getId(),
                MaterialResponse.from(mov.getMaterial()),
                mov.getOrdenTrabajo() == null ? null : mov.getOrdenTrabajo().getId(),
                mov.getTipo(),
                mov.getCantidad(),
                UsuarioResumenResponse.from(mov.getUsuario()),
                mov.getFecha());
    }
}
