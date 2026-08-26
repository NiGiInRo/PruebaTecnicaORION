package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.domain.model.Material;
import java.math.BigDecimal;

public record MaterialResponse(
        Long id,
        String codigo,
        String nombre,
        String unidadMedida,
        BigDecimal stockActual,
        BigDecimal stockMinimo,
        boolean stockBajo) {

    public static MaterialResponse from(Material material) {
        return new MaterialResponse(
                material.getId(),
                material.getCodigo(),
                material.getNombre(),
                material.getUnidadMedida(),
                material.getStockActual(),
                material.getStockMinimo(),
                material.tieneStockBajo());
    }
}
