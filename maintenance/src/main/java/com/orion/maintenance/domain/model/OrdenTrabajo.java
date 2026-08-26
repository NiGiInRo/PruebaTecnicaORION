package com.orion.maintenance.domain.model;

import com.orion.maintenance.domain.exception.TransicionInvalidaException;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "orden_trabajo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "activo_id", nullable = false)
    private Activo activo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOrdenTrabajo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadOrdenTrabajo prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrdenTrabajo estado;

    @Column(length = 1000)
    private String descripcion;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_programada")
    private LocalDate fechaProgramada;

    @Column(name = "fecha_inicio_ejecucion")
    private Instant fechaInicioEjecucion;

    @Column(name = "fecha_cierre")
    private Instant fechaCierre;

    @Column(name = "observaciones_cierre", length = 1000)
    private String observacionesCierre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigenOrdenTrabajo origen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creado_por_id")
    private Usuario creadoPor;

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrdenTrabajoCuadrilla> cuadrillasAsignadas = new ArrayList<>();

    public OrdenTrabajo(
            Activo activo,
            TipoOrdenTrabajo tipo,
            PrioridadOrdenTrabajo prioridad,
            String descripcion,
            LocalDate fechaProgramada,
            OrigenOrdenTrabajo origen,
            Usuario creadoPor) {
        this.activo = activo;
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.fechaProgramada = fechaProgramada;
        this.origen = origen;
        this.creadoPor = creadoPor;
        this.estado = EstadoOrdenTrabajo.ABIERTA;
    }

    public boolean esTerminal() {
        return estado == EstadoOrdenTrabajo.CERRADA || estado == EstadoOrdenTrabajo.CANCELADA;
    }

    public void asignarCuadrilla(Cuadrilla cuadrilla, RolCuadrillaEnOT rol) {
        if (estado != EstadoOrdenTrabajo.ABIERTA && estado != EstadoOrdenTrabajo.ASIGNADA) {
            throw new TransicionInvalidaException(
                    "No se pueden asignar cuadrillas a una OT en estado " + estado);
        }
        cuadrillasAsignadas.add(new OrdenTrabajoCuadrilla(this, cuadrilla, rol));
        this.estado = EstadoOrdenTrabajo.ASIGNADA;
    }

    public void iniciarEjecucion() {
        if (estado != EstadoOrdenTrabajo.ASIGNADA) {
            throw new TransicionInvalidaException(
                    "No se puede iniciar ejecución de una OT en estado " + estado);
        }
        this.estado = EstadoOrdenTrabajo.EN_EJECUCION;
        this.fechaInicioEjecucion = Instant.now();
    }

    public void cerrar(String observaciones) {
        if (estado != EstadoOrdenTrabajo.EN_EJECUCION) {
            throw new TransicionInvalidaException("No se puede cerrar una OT en estado " + estado);
        }
        this.estado = EstadoOrdenTrabajo.CERRADA;
        this.fechaCierre = Instant.now();
        this.observacionesCierre = observaciones;
    }

    public void cancelar(String motivo) {
        if (esTerminal()) {
            throw new TransicionInvalidaException("No se puede cancelar una OT en estado " + estado);
        }
        this.estado = EstadoOrdenTrabajo.CANCELADA;
        this.fechaCierre = Instant.now();
        this.observacionesCierre = motivo;
    }
}
