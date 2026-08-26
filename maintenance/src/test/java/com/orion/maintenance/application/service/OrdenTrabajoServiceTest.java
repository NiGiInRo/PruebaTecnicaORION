package com.orion.maintenance.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.orion.maintenance.application.exception.OrdenTrabajoActivaExistenteException;
import com.orion.maintenance.application.port.OrdenTrabajoRepository;
import com.orion.maintenance.application.port.UsuarioRepository;
import com.orion.maintenance.domain.model.Activo;
import com.orion.maintenance.domain.model.CorredorVial;
import com.orion.maintenance.domain.model.Cuadrilla;
import com.orion.maintenance.domain.model.EspecialidadCuadrilla;
import com.orion.maintenance.domain.model.EstadoActivo;
import com.orion.maintenance.domain.model.OrdenTrabajo;
import com.orion.maintenance.domain.model.PrioridadOrdenTrabajo;
import com.orion.maintenance.domain.model.RolCuadrillaEnOT;
import com.orion.maintenance.domain.model.TipoActivo;
import com.orion.maintenance.domain.model.TipoOrdenTrabajo;
import com.orion.maintenance.domain.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrdenTrabajoServiceTest {

    @Mock private OrdenTrabajoRepository ordenTrabajoRepository;
    @Mock private ActivoService activoService;
    @Mock private CuadrillaService cuadrillaService;
    @Mock private UsuarioRepository usuarioRepository;

    private OrdenTrabajoService service() {
        return new OrdenTrabajoService(
                ordenTrabajoRepository, activoService, cuadrillaService, usuarioRepository);
    }

    private Activo activoOperativo() {
        CorredorVial corredor = new CorredorVial("CV-01", "Autopista Norte", "Tramo 1");
        return new Activo("PMV-01", "Panel Norte", TipoActivo.PMV, corredor, null, null, null, null);
    }

    private Usuario supervisor() {
        return new Usuario("Supervisor Demo", "supervisor@orion.com", "hash", com.orion.maintenance.domain.model.Rol.SUPERVISOR);
    }

    @Test
    void crearUnaOtCorrectivaDejaElActivoFueraDeServicio() {
        Activo activo = activoOperativo();
        when(ordenTrabajoRepository.existsByActivoIdAndEstadoNotIn(anyLong(), any())).thenReturn(false);
        when(activoService.obtenerPorId(1L)).thenReturn(activo);
        when(usuarioRepository.findByEmail("supervisor@orion.com")).thenReturn(Optional.of(supervisor()));
        when(ordenTrabajoRepository.save(any(OrdenTrabajo.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdenTrabajo ot =
                service()
                        .crear(
                                1L,
                                TipoOrdenTrabajo.CORRECTIVO,
                                PrioridadOrdenTrabajo.ALTA,
                                "Falla",
                                null,
                                "supervisor@orion.com");

        assertThat(ot.getActivo().getEstado()).isEqualTo(EstadoActivo.FUERA_DE_SERVICIO);
    }

    @Test
    void crearUnaOtPreventivaNoCambiaElEstadoDelActivo() {
        Activo activo = activoOperativo();
        when(ordenTrabajoRepository.existsByActivoIdAndEstadoNotIn(anyLong(), any())).thenReturn(false);
        when(activoService.obtenerPorId(1L)).thenReturn(activo);
        when(usuarioRepository.findByEmail("supervisor@orion.com")).thenReturn(Optional.of(supervisor()));
        when(ordenTrabajoRepository.save(any(OrdenTrabajo.class))).thenAnswer(inv -> inv.getArgument(0));

        service()
                .crear(
                        1L,
                        TipoOrdenTrabajo.PREVENTIVO,
                        PrioridadOrdenTrabajo.BAJA,
                        "Mantenimiento programado",
                        null,
                        "supervisor@orion.com");

        assertThat(activo.getEstado()).isEqualTo(EstadoActivo.OPERATIVO);
    }

    @Test
    void noPermiteCrearUnaOtSiElActivoYaTieneUnaActiva() {
        when(ordenTrabajoRepository.existsByActivoIdAndEstadoNotIn(anyLong(), any())).thenReturn(true);

        assertThatThrownBy(
                        () ->
                                service()
                                        .crear(
                                                1L,
                                                TipoOrdenTrabajo.CORRECTIVO,
                                                PrioridadOrdenTrabajo.ALTA,
                                                "Falla",
                                                null,
                                                "supervisor@orion.com"))
                .isInstanceOf(OrdenTrabajoActivaExistenteException.class);
    }

    @Test
    void asignarCuadrillasDejaElActivoEnMantenimiento() {
        Activo activo = activoOperativo();
        OrdenTrabajo ot =
                new OrdenTrabajo(
                        activo,
                        TipoOrdenTrabajo.CORRECTIVO,
                        PrioridadOrdenTrabajo.ALTA,
                        "Falla",
                        null,
                        com.orion.maintenance.domain.model.OrigenOrdenTrabajo.MANUAL,
                        supervisor());
        Cuadrilla cuadrilla = new Cuadrilla("CUA-01", "Cuadrilla Norte", EspecialidadCuadrilla.ELECTRICA);
        when(ordenTrabajoRepository.findById(1L)).thenReturn(Optional.of(ot));
        when(cuadrillaService.obtenerPorId(1L)).thenReturn(cuadrilla);
        when(ordenTrabajoRepository.save(any(OrdenTrabajo.class))).thenAnswer(inv -> inv.getArgument(0));

        service()
                .asignarCuadrillas(
                        1L, List.of(new AsignacionCuadrillaInput(1L, RolCuadrillaEnOT.EJECUCION_TECNICA)));

        assertThat(activo.getEstado()).isEqualTo(EstadoActivo.EN_MANTENIMIENTO);
    }

    @Test
    void cerrarUnaOtDejaElActivoOperativo() {
        Activo activo = activoOperativo();
        activo.marcarFueraDeServicio();
        OrdenTrabajo ot =
                new OrdenTrabajo(
                        activo,
                        TipoOrdenTrabajo.CORRECTIVO,
                        PrioridadOrdenTrabajo.ALTA,
                        "Falla",
                        null,
                        com.orion.maintenance.domain.model.OrigenOrdenTrabajo.MANUAL,
                        supervisor());
        ot.asignarCuadrilla(new Cuadrilla("CUA-01", "Cuadrilla Norte", EspecialidadCuadrilla.ELECTRICA), RolCuadrillaEnOT.EJECUCION_TECNICA);
        ot.iniciarEjecucion();
        when(ordenTrabajoRepository.findById(1L)).thenReturn(Optional.of(ot));
        when(ordenTrabajoRepository.save(any(OrdenTrabajo.class))).thenAnswer(inv -> inv.getArgument(0));

        service().cerrar(1L, "Reparado");

        assertThat(activo.getEstado()).isEqualTo(EstadoActivo.OPERATIVO);
    }
}
