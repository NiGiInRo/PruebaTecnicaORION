package com.orion.maintenance.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Versión mínima de Cuadrilla para desbloquear la asignación en HU-002.
 * Técnicos miembros y disponibilidad derivada de OTs activas quedan para HU-003.
 */
@Entity
@Table(name = "cuadrilla")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cuadrilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EspecialidadCuadrilla especialidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCuadrilla estado;

    public Cuadrilla(String codigo, String nombre, EspecialidadCuadrilla especialidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.estado = EstadoCuadrilla.DISPONIBLE;
    }
}
