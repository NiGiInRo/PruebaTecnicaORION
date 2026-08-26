package com.orion.maintenance.application.service;

import com.orion.maintenance.application.port.ActivoRepository;
import com.orion.maintenance.application.port.OrdenTrabajoRepository;
import com.orion.maintenance.domain.model.Activo;
import com.orion.maintenance.domain.model.EstadoActivo;
import com.orion.maintenance.domain.model.EstadoOrdenTrabajo;
import com.orion.maintenance.domain.model.OrdenTrabajo;
import com.orion.maintenance.domain.model.TipoOrdenTrabajo;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consultas agregadas de solo lectura sobre Activo/OrdenTrabajo/Cuadrilla. No introduce
 * entidades ni persistencia propia (ver BACKLOG_REFINED.md HU-004). Con el volumen esperado
 * en este alcance no se justifica cache ni vistas materializadas (ver SCENARIO.md).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final ActivoRepository activoRepository;

    public DashboardIndicadores obtenerIndicadores() {
        List<OrdenTrabajo> ordenes = ordenTrabajoRepository.findAll();
        List<Activo> activos = activoRepository.findAll();

        Map<EstadoOrdenTrabajo, Long> otsPorEstado =
                ordenes.stream()
                        .collect(Collectors.groupingBy(OrdenTrabajo::getEstado, Collectors.counting()));

        Map<TipoOrdenTrabajo, Long> otsPorTipo =
                ordenes.stream()
                        .collect(Collectors.groupingBy(OrdenTrabajo::getTipo, Collectors.counting()));

        long operativos = contarPorEstado(activos, EstadoActivo.OPERATIVO);
        long fueraDeServicio = contarPorEstado(activos, EstadoActivo.FUERA_DE_SERVICIO);
        long enMantenimiento = contarPorEstado(activos, EstadoActivo.EN_MANTENIMIENTO);
        double porcentajeDisponibilidad = activos.isEmpty() ? 0.0 : (operativos * 100.0) / activos.size();

        Map<String, Long> otsPorCorredor =
                ordenes.stream()
                        .collect(
                                Collectors.groupingBy(
                                        ot -> ot.getActivo().getCorredor().getCodigo(), Collectors.counting()));

        Map<String, Long> cargaPorCuadrilla =
                ordenes.stream()
                        .filter(ot -> !ot.esTerminal())
                        .flatMap(ot -> ot.getCuadrillasAsignadas().stream())
                        .collect(Collectors.groupingBy(otc -> otc.getCuadrilla().getCodigo(), Collectors.counting()));

        Double mttrHoras = calcularMttrHoras(ordenes);

        return new DashboardIndicadores(
                otsPorEstado,
                otsPorTipo,
                operativos,
                fueraDeServicio,
                enMantenimiento,
                porcentajeDisponibilidad,
                otsPorCorredor,
                cargaPorCuadrilla,
                mttrHoras);
    }

    private long contarPorEstado(List<Activo> activos, EstadoActivo estado) {
        return activos.stream().filter(a -> a.getEstado() == estado).count();
    }

    private Double calcularMttrHoras(List<OrdenTrabajo> ordenes) {
        OptionalDouble promedioMinutos =
                ordenes.stream()
                        .filter(ot -> ot.getEstado() == EstadoOrdenTrabajo.CERRADA)
                        .filter(ot -> ot.getFechaCreacion() != null && ot.getFechaCierre() != null)
                        .mapToLong(
                                ot -> Duration.between(ot.getFechaCreacion(), ot.getFechaCierre()).toMinutes())
                        .average();
        return promedioMinutos.isPresent() ? promedioMinutos.getAsDouble() / 60.0 : null;
    }
}
