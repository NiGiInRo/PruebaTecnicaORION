package com.orion.maintenance.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.orion.maintenance.domain.exception.TransicionInvalidaException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class CuadrillaTest {

    private Usuario tecnico(String email) {
        return new Usuario("Tecnico " + email, email, "hash", Rol.TECNICO);
    }

    private Cuadrilla cuadrilla() {
        return new Cuadrilla("CUA-01", "Cuadrilla Norte", EspecialidadCuadrilla.ELECTRICA);
    }

    @Test
    void unaCuadrillaNuevaNaceDisponible() {
        assertThat(cuadrilla().getEstado()).isEqualTo(EstadoCuadrilla.DISPONIBLE);
    }

    @Test
    void agregarUnTecnicoLoSumaALaLista() {
        Cuadrilla cuadrilla = cuadrilla();

        cuadrilla.agregarTecnico(tecnico("tec1@orion.com"));

        assertThat(cuadrilla.getTecnicos()).hasSize(1);
    }

    @Test
    void quitarUnTecnicoLoRemueveDeLaLista() {
        Cuadrilla cuadrilla = cuadrilla();
        Usuario tecnico = tecnico("tec1@orion.com");
        ReflectionTestUtils.setField(tecnico, "id", 1L);
        cuadrilla.agregarTecnico(tecnico);

        cuadrilla.quitarTecnico(1L);

        assertThat(cuadrilla.getTecnicos()).isEmpty();
    }

    @Test
    void quitarAlLiderTambienLoDesigna() {
        Cuadrilla cuadrilla = cuadrilla();
        Usuario tecnico = tecnico("tec1@orion.com");
        ReflectionTestUtils.setField(tecnico, "id", 1L);
        cuadrilla.agregarTecnico(tecnico);
        cuadrilla.designarLider(tecnico);

        cuadrilla.quitarTecnico(1L);

        assertThat(cuadrilla.getLider()).isNull();
    }

    @Test
    void noSePuedeDesignarLiderAQuienNoEsMiembro() {
        Cuadrilla cuadrilla = cuadrilla();
        Usuario tecnico = tecnico("tec1@orion.com");
        ReflectionTestUtils.setField(tecnico, "id", 1L);

        assertThatThrownBy(() -> cuadrilla.designarLider(tecnico))
                .isInstanceOf(TransicionInvalidaException.class);
    }

    @Test
    void noSePuedeDesactivarUnaCuadrillaEnMision() {
        Cuadrilla cuadrilla = cuadrilla();
        cuadrilla.marcarEnMision();

        assertThatThrownBy(cuadrilla::desactivar).isInstanceOf(TransicionInvalidaException.class);
    }

    @Test
    void desactivarUnaCuadrillaDisponibleFunciona() {
        Cuadrilla cuadrilla = cuadrilla();

        cuadrilla.desactivar();

        assertThat(cuadrilla.getEstado()).isEqualTo(EstadoCuadrilla.INACTIVA);
    }
}
