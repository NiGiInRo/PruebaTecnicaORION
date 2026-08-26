package com.orion.maintenance.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.orion.maintenance.domain.exception.TransicionInvalidaException;
import org.junit.jupiter.api.Test;

class OrdenTrabajoTest {

    private CorredorVial corredor() {
        return new CorredorVial("CV-01", "Autopista Norte", "Tramo 1");
    }

    private Activo activo() {
        return new Activo("PMV-01", "Panel Norte", TipoActivo.PMV, corredor(), null, null, null, null);
    }

    private Cuadrilla cuadrilla(String codigo) {
        return new Cuadrilla(codigo, "Cuadrilla " + codigo, EspecialidadCuadrilla.ELECTRICA);
    }

    private OrdenTrabajo otAbierta() {
        return new OrdenTrabajo(
                activo(),
                TipoOrdenTrabajo.CORRECTIVO,
                PrioridadOrdenTrabajo.ALTA,
                "Falla de energía",
                null,
                OrigenOrdenTrabajo.MANUAL,
                null);
    }

    @Test
    void unaOrdenNuevaNaceAbierta() {
        assertThat(otAbierta().getEstado()).isEqualTo(EstadoOrdenTrabajo.ABIERTA);
    }

    @Test
    void asignarUnaCuadrillaDesdeAbiertaPasaAAsignada() {
        OrdenTrabajo ot = otAbierta();

        ot.asignarCuadrilla(cuadrilla("CUA-01"), RolCuadrillaEnOT.EJECUCION_TECNICA);

        assertThat(ot.getEstado()).isEqualTo(EstadoOrdenTrabajo.ASIGNADA);
        assertThat(ot.getCuadrillasAsignadas()).hasSize(1);
    }

    @Test
    void sePuedeAsignarMasDeUnaCuadrillaEstandoYaAsignada() {
        OrdenTrabajo ot = otAbierta();
        ot.asignarCuadrilla(cuadrilla("CUA-01"), RolCuadrillaEnOT.EJECUCION_TECNICA);

        ot.asignarCuadrilla(cuadrilla("CUA-02"), RolCuadrillaEnOT.SENALIZACION);

        assertThat(ot.getEstado()).isEqualTo(EstadoOrdenTrabajo.ASIGNADA);
        assertThat(ot.getCuadrillasAsignadas()).hasSize(2);
    }

    @Test
    void noSePuedeAsignarUnaCuadrillaAUnaOtEnEjecucion() {
        OrdenTrabajo ot = otAbierta();
        ot.asignarCuadrilla(cuadrilla("CUA-01"), RolCuadrillaEnOT.EJECUCION_TECNICA);
        ot.iniciarEjecucion();

        assertThatThrownBy(() -> ot.asignarCuadrilla(cuadrilla("CUA-02"), RolCuadrillaEnOT.SENALIZACION))
                .isInstanceOf(TransicionInvalidaException.class);
    }

    @Test
    void noSePuedeIniciarEjecucionSinCuadrillaAsignada() {
        OrdenTrabajo ot = otAbierta();

        assertThatThrownBy(ot::iniciarEjecucion).isInstanceOf(TransicionInvalidaException.class);
    }

    @Test
    void iniciarEjecucionDesdeAsignadaPasaAEnEjecucion() {
        OrdenTrabajo ot = otAbierta();
        ot.asignarCuadrilla(cuadrilla("CUA-01"), RolCuadrillaEnOT.EJECUCION_TECNICA);

        ot.iniciarEjecucion();

        assertThat(ot.getEstado()).isEqualTo(EstadoOrdenTrabajo.EN_EJECUCION);
        assertThat(ot.getFechaInicioEjecucion()).isNotNull();
    }

    @Test
    void noSePuedeCerrarUnaOtQueNoEstaEnEjecucion() {
        OrdenTrabajo ot = otAbierta();
        ot.asignarCuadrilla(cuadrilla("CUA-01"), RolCuadrillaEnOT.EJECUCION_TECNICA);

        assertThatThrownBy(() -> ot.cerrar("Reparado")).isInstanceOf(TransicionInvalidaException.class);
    }

    @Test
    void cerrarDesdeEnEjecucionPasaACerradaConObservaciones() {
        OrdenTrabajo ot = otAbierta();
        ot.asignarCuadrilla(cuadrilla("CUA-01"), RolCuadrillaEnOT.EJECUCION_TECNICA);
        ot.iniciarEjecucion();

        ot.cerrar("Reparado sin novedad");

        assertThat(ot.getEstado()).isEqualTo(EstadoOrdenTrabajo.CERRADA);
        assertThat(ot.getFechaCierre()).isNotNull();
        assertThat(ot.getObservacionesCierre()).isEqualTo("Reparado sin novedad");
    }

    @Test
    void cancelarEsPosibleDesdeCualquierEstadoNoTerminal() {
        OrdenTrabajo ot = otAbierta();

        ot.cancelar("Ya no se requiere");

        assertThat(ot.getEstado()).isEqualTo(EstadoOrdenTrabajo.CANCELADA);
        assertThat(ot.esTerminal()).isTrue();
    }

    @Test
    void noSePuedeCancelarUnaOtYaCerrada() {
        OrdenTrabajo ot = otAbierta();
        ot.asignarCuadrilla(cuadrilla("CUA-01"), RolCuadrillaEnOT.EJECUCION_TECNICA);
        ot.iniciarEjecucion();
        ot.cerrar("Reparado");

        assertThatThrownBy(() -> ot.cancelar("Tarde")).isInstanceOf(TransicionInvalidaException.class);
    }
}
