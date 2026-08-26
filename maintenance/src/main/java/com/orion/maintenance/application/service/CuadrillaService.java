package com.orion.maintenance.application.service;

import com.orion.maintenance.application.exception.CodigoDuplicadoException;
import com.orion.maintenance.application.exception.RecursoNoEncontradoException;
import com.orion.maintenance.application.port.CuadrillaRepository;
import com.orion.maintenance.domain.model.Cuadrilla;
import com.orion.maintenance.domain.model.EspecialidadCuadrilla;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CuadrillaService {

    private final CuadrillaRepository cuadrillaRepository;

    public Cuadrilla crear(String codigo, String nombre, EspecialidadCuadrilla especialidad) {
        if (cuadrillaRepository.existsByCodigo(codigo)) {
            throw new CodigoDuplicadoException("Ya existe una cuadrilla con código " + codigo);
        }
        return cuadrillaRepository.save(new Cuadrilla(codigo, nombre, especialidad));
    }

    @Transactional(readOnly = true)
    public Cuadrilla obtenerPorId(Long id) {
        return cuadrillaRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuadrilla no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cuadrilla> listar() {
        return cuadrillaRepository.findAll();
    }
}
