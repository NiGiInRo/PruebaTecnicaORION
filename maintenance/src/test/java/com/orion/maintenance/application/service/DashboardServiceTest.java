package com.orion.maintenance.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.when;

import com.orion.maintenance.application.port.ActivoRepository;
import com.orion.maintenance.application.port.OrdenTrabajoRepository;
import com.orion.maintenance.domain.model.Activo;
import com.orion.maintenance.domain.model.CorredorVial;
import com.orion.maintenance.domain.model.Cuadrilla;
import com.orion.maintenance.domain.model.EspecialidadCuadrilla;
import com.orion.maintenance.domain.model.EstadoOrdenTrabajo;
import com.orion.maintenance.domain.model.OrdenTrabajo;
import com.orion.maintenance.domain.model.OrigenOrdenTrabajo;
import com.orion.maintenance.domain.model.PrioridadOrdenTrabajo;
import com.orion.maintenance.domain.model.Rol;
import com.orion.maintenance.domain.model.RolCuadrillaEnOT;
import com.orion.maintenance.domain.model.TipoActivo;
import com.orion.maintenance.domain.model.TipoOrdenTrabajo;
import com.orion.maintenance.domain.model.Usuario;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private OrdenTrabajoRepository ordenTrabajoRepository;
    @Mock private ActivoRepository activoRepository;

    private DashboardService service() {
        return new DashboardService(ordenTrabajoRepository, activoRepository);
    }

    private CorredorVial corredor() {
        return new CorredorVial("CV-01", "Autopista Norte", "Tramo 1");
    }

    private Activo activo(CorredorVial corredor) {
        return new Activo("PMV-01", "Panel Norte", TipoActivo.PMV, corredor, null, null, null, null);
    }

    private Usuario supervisor() {
        return new Usuario("Supervisor Demo", "supervisor@orion.com", "hash", Rol.SUPERVISOR);
    }

    private OrdenTrabajo ot(Activo activo, TipoOrdenTrabajo tipo) {
        return new OrdenTrabajo(
                activo, tipo, PrioridadOrdenTrabajo.ALTA, "desc", null, OrigenOrdenTrabajo.MANUAL, supervisor());
    }

    @Test
    void cuentaOtsPorEstadoYTipo() {
        CorredorVial corredor = corredor();
        Activo activo = activo(corredor);
        OrdenTrabajo abierta = ot(activo, TipoOrdenTrabajo.CORRECTIVO);
        OrdenTrabajo preventiva = ot(activo, TipoOrdenTrabajo.PREVENTIVO);
        when(ordenTrabajoRepository.findAll()).thenReturn(List.of(abierta, preventiva));
        when(activoRepository.findAll()).thenReturn(List.of(activo));

        DashboardIndicadores indicadores = service().obtenerIndicadores();

        assertThat(indicadores.otsPorEstado().get(EstadoOrdenTrabajo.ABIERTA)).isEqualTo(2L);
        assertThat(indicadores.otsPorTipo().get(TipoOrdenTrabajo.CORRECTIVO)).isEqualTo(1L);
        assertThat(indicadores.otsPorTipo().get(TipoOrdenTrabajo.PREVENTIVO)).isEqualTo(1L);
    }

    @Test
    void calculaPorcentajeDeDisponibilidad() {
        CorredorVial corredor = corredor();
        Activo operativo = activo(corredor);
        Activo fueraDeServicio = activo(corredor);
        fueraDeServicio.marcarFueraDeServicio();
        when(ordenTrabajoRepository.findAll()).thenReturn(List.of());
        when(activoRepository.findAll()).thenReturn(List.of(operativo, fueraDeServicio));

        DashboardIndicadores indicadores = service().obtenerIndicadores();

        assertThat(indicadores.porcentajeDisponibilidadActivos()).isEqualTo(50.0);
        assertThat(indicadores.activosOperativos()).isEqualTo(1);
        assertThat(indicadores.activosFueraDeServicio()).isEqualTo(1);
    }

    @Test
    void cargaPorCuadrillaSoloCuentaOtsNoTerminales() {
        CorredorVial corredor = corredor();
        Activo activo1 = activo(corredor);
        Activo activo2 = activo(corredor);
        Cuadrilla cuadrilla = new Cuadrilla("CUA-01", "Cuadrilla Norte", EspecialidadCuadrilla.ELECTRICA);

        OrdenTrabajo activa = ot(activo1, TipoOrdenTrabajo.CORRECTIVO);
        activa.asignarCuadrilla(cuadrilla, RolCuadrillaEnOT.EJECUCION_TECNICA);

        OrdenTrabajo cerrada = ot(activo2, TipoOrdenTrabajo.CORRECTIVO);
        cerrada.asignarCuadrilla(cuadrilla, RolCuadrillaEnOT.EJECUCION_TECNICA);
        cerrada.iniciarEjecucion();
        cerrada.cerrar("listo");

        when(ordenTrabajoRepository.findAll()).thenReturn(List.of(activa, cerrada));
        when(activoRepository.findAll()).thenReturn(List.of(activo1, activo2));

        DashboardIndicadores indicadores = service().obtenerIndicadores();

        assertThat(indicadores.cargaPorCuadrilla().get("CUA-01")).isEqualTo(1L);
    }

    @Test
    void calculaMttrEnHorasSoloConOtsCerradas() {
        CorredorVial corredor = corredor();
        Activo activo = activo(corredor);
        OrdenTrabajo cerrada = ot(activo, TipoOrdenTrabajo.CORRECTIVO);
        ReflectionTestUtils.setField(cerrada, "fechaCreacion", Instant.now().minus(2, ChronoUnit.HOURS));
        Cuadrilla cuadrilla = new Cuadrilla("CUA-01", "Cuadrilla Norte", EspecialidadCuadrilla.ELECTRICA);
        cerrada.asignarCuadrilla(cuadrilla, RolCuadrillaEnOT.EJECUCION_TECNICA);
        cerrada.iniciarEjecucion();
        cerrada.cerrar("listo");

        OrdenTrabajo abierta = ot(activo, TipoOrdenTrabajo.CORRECTIVO);

        when(ordenTrabajoRepository.findAll()).thenReturn(List.of(cerrada, abierta));
        when(activoRepository.findAll()).thenReturn(List.of(activo));

        DashboardIndicadores indicadores = service().obtenerIndicadores();

        assertThat(indicadores.tiempoPromedioResolucionHoras()).isCloseTo(2.0, within(0.05));
    }

    @Test
    void mttrEsNuloSiNoHayOtsCerradas() {
        CorredorVial corredor = corredor();
        Activo activo = activo(corredor);
        OrdenTrabajo abierta = ot(activo, TipoOrdenTrabajo.CORRECTIVO);

        when(ordenTrabajoRepository.findAll()).thenReturn(List.of(abierta));
        when(activoRepository.findAll()).thenReturn(List.of(activo));

        DashboardIndicadores indicadores = service().obtenerIndicadores();

        assertThat(indicadores.tiempoPromedioResolucionHoras()).isNull();
    }
}
