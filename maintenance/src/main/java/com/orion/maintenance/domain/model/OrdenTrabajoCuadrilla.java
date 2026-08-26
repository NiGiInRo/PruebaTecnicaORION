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
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "orden_trabajo_cuadrilla")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrdenTrabajoCuadrilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "orden_trabajo_id", nullable = false)
    private OrdenTrabajo ordenTrabajo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cuadrilla_id", nullable = false)
    private Cuadrilla cuadrilla;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolCuadrillaEnOT rol;

    @CreationTimestamp
    @Column(name = "fecha_asignacion", updatable = false)
    private Instant fechaAsignacion;

    public OrdenTrabajoCuadrilla(OrdenTrabajo ordenTrabajo, Cuadrilla cuadrilla, RolCuadrillaEnOT rol) {
        this.ordenTrabajo = ordenTrabajo;
        this.cuadrilla = cuadrilla;
        this.rol = rol;
    }
}
