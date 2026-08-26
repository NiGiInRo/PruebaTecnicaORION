package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.Activo;
import com.orion.maintenance.domain.model.EstadoActivo;
import com.orion.maintenance.domain.model.TipoActivo;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ActivoResponse(
        Long id,
        String codigo,
        String nombre,
        TipoActivo tipo,
        CorredorVialResponse corredor,
        BigDecimal pkKilometraje,
        String fabricante,
        String modelo,
        LocalDate fechaInstalacion,
        EstadoActivo estado) {

    public static ActivoResponse from(Activo activo) {
        return new ActivoResponse(
                activo.getId(),
                activo.getCodigo(),
                activo.getNombre(),
                activo.getTipo(),
                CorredorVialResponse.from(activo.getCorredor()),
                activo.getPkKilometraje(),
                activo.getFabricante(),
                activo.getModelo(),
                activo.getFechaInstalacion(),
                activo.getEstado());
    }
}
