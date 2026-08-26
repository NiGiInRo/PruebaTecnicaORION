package com.orion.maintenance.application.service;

import com.orion.maintenance.application.exception.CodigoDuplicadoException;
import com.orion.maintenance.application.exception.RecursoNoEncontradoException;
import com.orion.maintenance.application.port.ActivoRepository;
import com.orion.maintenance.application.port.ActivoSpecifications;
import com.orion.maintenance.domain.model.Activo;
import com.orion.maintenance.domain.model.CorredorVial;
import com.orion.maintenance.domain.model.EstadoActivo;
import com.orion.maintenance.domain.model.TipoActivo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivoService {

    private final ActivoRepository activoRepository;
    private final CorredorVialService corredorVialService;

    public Activo crear(
            String codigo,
            String nombre,
            TipoActivo tipo,
            Long corredorId,
            BigDecimal pkKilometraje,
            String fabricante,
            String modelo,
            LocalDate fechaInstalacion) {
        if (activoRepository.existsByCodigo(codigo)) {
            throw new CodigoDuplicadoException("Ya existe un activo con código " + codigo);
        }
        CorredorVial corredor = corredorVialService.obtenerPorId(corredorId);
        Activo activo =
                new Activo(
                        codigo, nombre, tipo, corredor, pkKilometraje, fabricante, modelo, fechaInstalacion);
        return activoRepository.save(activo);
    }

    public Activo actualizar(
            Long id,
            String nombre,
            TipoActivo tipo,
            Long corredorId,
            BigDecimal pkKilometraje,
            String fabricante,
            String modelo,
            LocalDate fechaInstalacion) {
        Activo activo = obtenerPorId(id);
        CorredorVial corredor = corredorVialService.obtenerPorId(corredorId);
        activo.actualizarDatos(nombre, tipo, corredor, pkKilometraje, fabricante, modelo, fechaInstalacion);
        return activoRepository.save(activo);
    }

    @Transactional(readOnly = true)
    public Activo obtenerPorId(Long id) {
        return activoRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Activo no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Activo> listar(Long corredorId, TipoActivo tipo, EstadoActivo estado) {
        return activoRepository.findAll(ActivoSpecifications.filtros(corredorId, tipo, estado));
    }
}
