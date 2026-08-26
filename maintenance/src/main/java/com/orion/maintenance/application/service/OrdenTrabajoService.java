package com.orion.maintenance.application.service;

import com.orion.maintenance.application.exception.OrdenTrabajoActivaExistenteException;
import com.orion.maintenance.application.exception.RecursoNoEncontradoException;
import com.orion.maintenance.application.port.OrdenTrabajoRepository;
import com.orion.maintenance.application.port.OrdenTrabajoSpecifications;
import com.orion.maintenance.application.port.UsuarioRepository;
import com.orion.maintenance.domain.model.Activo;
import com.orion.maintenance.domain.model.Cuadrilla;
import com.orion.maintenance.domain.model.EstadoOrdenTrabajo;
import com.orion.maintenance.domain.model.OrdenTrabajo;
import com.orion.maintenance.domain.model.OrigenOrdenTrabajo;
import com.orion.maintenance.domain.model.PrioridadOrdenTrabajo;
import com.orion.maintenance.domain.model.TipoOrdenTrabajo;
import com.orion.maintenance.domain.model.Usuario;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdenTrabajoService {

    private static final List<EstadoOrdenTrabajo> ESTADOS_TERMINALES =
            List.of(EstadoOrdenTrabajo.CERRADA, EstadoOrdenTrabajo.CANCELADA);

    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final ActivoService activoService;
    private final CuadrillaService cuadrillaService;
    private final UsuarioRepository usuarioRepository;

    public OrdenTrabajo crear(
            Long activoId,
            TipoOrdenTrabajo tipo,
            PrioridadOrdenTrabajo prioridad,
            String descripcion,
            LocalDate fechaProgramada,
            String creadoPorEmail) {
        if (ordenTrabajoRepository.existsByActivoIdAndEstadoNotIn(activoId, ESTADOS_TERMINALES)) {
            throw new OrdenTrabajoActivaExistenteException(
                    "El activo " + activoId + " ya tiene una orden de trabajo activa");
        }

        Activo activo = activoService.obtenerPorId(activoId);
        Usuario creadoPor =
                usuarioRepository
                        .findByEmail(creadoPorEmail)
                        .orElseThrow(
                                () -> new RecursoNoEncontradoException("Usuario no encontrado: " + creadoPorEmail));

        OrdenTrabajo ot =
                new OrdenTrabajo(
                        activo, tipo, prioridad, descripcion, fechaProgramada, OrigenOrdenTrabajo.MANUAL, creadoPor);

        if (tipo == TipoOrdenTrabajo.CORRECTIVO) {
            activo.marcarFueraDeServicio();
        }

        return ordenTrabajoRepository.save(ot);
    }

    public OrdenTrabajo asignarCuadrillas(Long otId, List<AsignacionCuadrillaInput> asignaciones) {
        OrdenTrabajo ot = obtenerPorId(otId);
        for (AsignacionCuadrillaInput asignacion : asignaciones) {
            Cuadrilla cuadrilla = cuadrillaService.obtenerPorId(asignacion.cuadrillaId());
            ot.asignarCuadrilla(cuadrilla, asignacion.rol());
        }
        ot.getActivo().marcarEnMantenimiento();
        return ordenTrabajoRepository.save(ot);
    }

    public OrdenTrabajo iniciarEjecucion(Long otId) {
        OrdenTrabajo ot = obtenerPorId(otId);
        ot.iniciarEjecucion();
        return ordenTrabajoRepository.save(ot);
    }

    public OrdenTrabajo cerrar(Long otId, String observaciones) {
        OrdenTrabajo ot = obtenerPorId(otId);
        ot.cerrar(observaciones);
        ot.getActivo().marcarOperativo();
        return ordenTrabajoRepository.save(ot);
    }

    public OrdenTrabajo cancelar(Long otId, String motivo) {
        OrdenTrabajo ot = obtenerPorId(otId);
        ot.cancelar(motivo);
        ot.getActivo().marcarOperativo();
        return ordenTrabajoRepository.save(ot);
    }

    @Transactional(readOnly = true)
    public OrdenTrabajo obtenerPorId(Long id) {
        return ordenTrabajoRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden de trabajo no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<OrdenTrabajo> listar(
            Long activoId, EstadoOrdenTrabajo estado, TipoOrdenTrabajo tipo, PrioridadOrdenTrabajo prioridad) {
        return ordenTrabajoRepository.findAll(
                OrdenTrabajoSpecifications.filtros(activoId, estado, tipo, prioridad));
    }
}
