package com.orion.maintenance.application.service;

import com.orion.maintenance.domain.model.EstadoOrdenTrabajo;
import com.orion.maintenance.domain.model.TipoOrdenTrabajo;
import java.util.Map;

public record DashboardIndicadores(
        Map<EstadoOrdenTrabajo, Long> otsPorEstado,
        Map<TipoOrdenTrabajo, Long> otsPorTipo,
        long activosOperativos,
        long activosFueraDeServicio,
        long activosEnMantenimiento,
        double porcentajeDisponibilidadActivos,
        Map<String, Long> otsPorCorredor,
        Map<String, Long> cargaPorCuadrilla,
        Double tiempoPromedioResolucionHoras) {}
