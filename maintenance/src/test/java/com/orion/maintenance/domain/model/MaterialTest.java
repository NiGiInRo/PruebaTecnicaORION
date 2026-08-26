package com.orion.maintenance.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.orion.maintenance.domain.exception.StockInsuficienteException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MaterialTest {

    private Material material() {
        return new Material("MAT-01", "Lámpara LED PMV", "unidad", new BigDecimal("5"));
    }

    @Test
    void unMaterialNuevoNaceConStockCero() {
        assertThat(material().getStockActual()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void registrarEntradaAumentaElStock() {
        Material material = material();

        material.registrarEntrada(new BigDecimal("10"));

        assertThat(material.getStockActual()).isEqualByComparingTo(new BigDecimal("10"));
    }

    @Test
    void registrarSalidaDisminuyeElStockCuandoHaySuficiente() {
        Material material = material();
        material.registrarEntrada(new BigDecimal("10"));

        material.registrarSalida(new BigDecimal("4"));

        assertThat(material.getStockActual()).isEqualByComparingTo(new BigDecimal("6"));
    }

    @Test
    void registrarSalidaExactaAlStockDisponibleFunciona() {
        Material material = material();
        material.registrarEntrada(new BigDecimal("10"));

        material.registrarSalida(new BigDecimal("10"));

        assertThat(material.getStockActual()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void noPermiteRegistrarSalidaMayorAlStockDisponible() {
        Material material = material();
        material.registrarEntrada(new BigDecimal("5"));

        assertThatThrownBy(() -> material.registrarSalida(new BigDecimal("6")))
                .isInstanceOf(StockInsuficienteException.class);
        assertThat(material.getStockActual()).isEqualByComparingTo(new BigDecimal("5"));
    }

    @Test
    void tieneStockBajoCuandoElStockActualEsMenorOIgualAlMinimo() {
        Material material = material();
        material.registrarEntrada(new BigDecimal("5"));

        assertThat(material.tieneStockBajo()).isTrue();

        material.registrarEntrada(new BigDecimal("1"));

        assertThat(material.tieneStockBajo()).isFalse();
    }
}
