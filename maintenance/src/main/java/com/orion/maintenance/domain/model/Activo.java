package com.orion.maintenance.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Activo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoActivo tipo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "corredor_id", nullable = false)
    private CorredorVial corredor;

    @Column(name = "pk_kilometraje")
    private BigDecimal pkKilometraje;

    private String fabricante;

    private String modelo;

    @Column(name = "fecha_instalacion")
    private LocalDate fechaInstalacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoActivo estado;

    public Activo(
            String codigo,
            String nombre,
            TipoActivo tipo,
            CorredorVial corredor,
            BigDecimal pkKilometraje,
            String fabricante,
            String modelo,
            LocalDate fechaInstalacion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.corredor = corredor;
        this.pkKilometraje = pkKilometraje;
        this.fabricante = fabricante;
        this.modelo = modelo;
        this.fechaInstalacion = fechaInstalacion;
        this.estado = EstadoActivo.OPERATIVO;
    }

    public void actualizarDatos(
            String nombre,
            TipoActivo tipo,
            CorredorVial corredor,
            BigDecimal pkKilometraje,
            String fabricante,
            String modelo,
            LocalDate fechaInstalacion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.corredor = corredor;
        this.pkKilometraje = pkKilometraje;
        this.fabricante = fabricante;
        this.modelo = modelo;
        this.fechaInstalacion = fechaInstalacion;
    }
}
