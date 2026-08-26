package com.orion.maintenance.domain.model;

import com.orion.maintenance.domain.exception.StockInsuficienteException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "material")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "unidad_medida", nullable = false)
    private String unidadMedida;

    @Column(name = "stock_actual", nullable = false)
    private BigDecimal stockActual;

    @Column(name = "stock_minimo", nullable = false)
    private BigDecimal stockMinimo;

    public Material(String codigo, String nombre, String unidadMedida, BigDecimal stockMinimo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.stockMinimo = stockMinimo;
        this.stockActual = BigDecimal.ZERO;
    }

    public void registrarEntrada(BigDecimal cantidad) {
        this.stockActual = this.stockActual.add(cantidad);
    }

    public void registrarSalida(BigDecimal cantidad) {
        if (cantidad.compareTo(stockActual) > 0) {
            throw new StockInsuficienteException(
                    "Stock insuficiente de "
                            + codigo
                            + ": disponible "
                            + stockActual
                            + ", solicitado "
                            + cantidad);
        }
        this.stockActual = this.stockActual.subtract(cantidad);
    }

    public boolean tieneStockBajo() {
        return stockActual.compareTo(stockMinimo) <= 0;
    }
}
