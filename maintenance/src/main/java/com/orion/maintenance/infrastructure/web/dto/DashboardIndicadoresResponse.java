package com.orion.maintenance.infrastructure.web.dto;

import com.orion.maintenance.application.service.DashboardIndicadores;
import com.orion.maintenance.domain.model.EstadoOrdenTrabajo;
import com.orion.maintenance.domain.model.TipoOrdenTrabajo;
import java.util.Map;

public record DashboardIndicadoresResponse(
        Map<EstadoOrdenTrabajo, Long> otsPorEstado,
        Map<TipoOrdenTrabajo, Long> otsPorTipo,
        long activosOperativos,
        long activosFueraDeServicio,
        long activosEnMantenimiento,
        double porcentajeDisponibilidadActivos,
        Map<String, Long> otsPorCorredor,
        Map<String, Long> cargaPorCuadrilla,
        Double tiempoPromedioResolucionHoras) {

    public static DashboardIndicadoresResponse from(DashboardIndicadores indicadores) {
        return new DashboardIndicadoresResponse(
                indicadores.otsPorEstado(),
                indicadores.otsPorTipo(),
                indicadores.activosOperativos(),
                indicadores.activosFueraDeServicio(),
                indicadores.activosEnMantenimiento(),
                indicadores.porcentajeDisponibilidadActivos(),
                indicadores.otsPorCorredor(),
                indicadores.cargaPorCuadrilla(),
                indicadores.tiempoPromedioResolucionHoras());
    }
}
