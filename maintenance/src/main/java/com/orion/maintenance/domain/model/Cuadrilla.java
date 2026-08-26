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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lider_id")
    private Usuario lider;

    @OneToMany(mappedBy = "cuadrilla", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<CuadrillaTecnico> tecnicos = new ArrayList<>();

    public Cuadrilla(String codigo, String nombre, EspecialidadCuadrilla especialidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.estado = EstadoCuadrilla.DISPONIBLE;
    }

    public void agregarTecnico(Usuario tecnico) {
        tecnicos.add(new CuadrillaTecnico(this, tecnico));
    }

    public void quitarTecnico(Long usuarioId) {
        tecnicos.removeIf(ct -> ct.getUsuario().getId().equals(usuarioId));
        if (lider != null && lider.getId().equals(usuarioId)) {
            lider = null;
        }
    }

    public void designarLider(Usuario tecnico) {
        boolean esMiembro = tecnicos.stream().anyMatch(ct -> ct.getUsuario().getId().equals(tecnico.getId()));
        if (!esMiembro) {
            throw new TransicionInvalidaException("El usuario no es miembro de la cuadrilla");
        }
        this.lider = tecnico;
    }

    public void marcarEnMision() {
        this.estado = EstadoCuadrilla.EN_MISION;
    }

    public void marcarDisponible() {
        this.estado = EstadoCuadrilla.DISPONIBLE;
    }

    public void activar() {
        this.estado = EstadoCuadrilla.DISPONIBLE;
    }

    public void desactivar() {
        if (estado == EstadoCuadrilla.EN_MISION) {
            throw new TransicionInvalidaException("No se puede desactivar una cuadrilla en misión");
        }
        this.estado = EstadoCuadrilla.INACTIVA;
    }
}
