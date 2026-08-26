package com.orion.maintenance.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/** Membresía de un técnico en una cuadrilla. Un técnico solo puede tener una membresía activa
 * a la vez (unicidad de usuario_id a nivel de BD) — ver justificación en BACKLOG_REFINED.md HU-003. */
@Entity
@Table(name = "cuadrilla_tecnico")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CuadrillaTecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cuadrilla_id", nullable = false)
    private Cuadrilla cuadrilla;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "fecha_asignacion", updatable = false)
    private Instant fechaAsignacion;

    public CuadrillaTecnico(Cuadrilla cuadrilla, Usuario usuario) {
        this.cuadrilla = cuadrilla;
        this.usuario = usuario;
    }
}
