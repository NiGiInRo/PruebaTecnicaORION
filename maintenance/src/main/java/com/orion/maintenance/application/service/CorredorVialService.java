package com.orion.maintenance.application.service;

import com.orion.maintenance.application.exception.CodigoDuplicadoException;
import com.orion.maintenance.application.exception.RecursoNoEncontradoException;
import com.orion.maintenance.application.port.CorredorVialRepository;
import com.orion.maintenance.domain.model.CorredorVial;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CorredorVialService {

    private final CorredorVialRepository corredorVialRepository;

    public CorredorVial crear(String codigo, String nombre, String descripcion) {
        if (corredorVialRepository.existsByCodigo(codigo)) {
            throw new CodigoDuplicadoException("Ya existe un corredor vial con código " + codigo);
        }
        return corredorVialRepository.save(new CorredorVial(codigo, nombre, descripcion));
    }

    public CorredorVial actualizar(Long id, String nombre, String descripcion) {
        CorredorVial corredor = obtenerPorId(id);
        corredor.actualizar(nombre, descripcion);
        return corredorVialRepository.save(corredor);
    }

    @Transactional(readOnly = true)
    public CorredorVial obtenerPorId(Long id) {
        return corredorVialRepository
                .findById(id)
                .orElseThrow(
                        () -> new RecursoNoEncontradoException("Corredor vial no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<CorredorVial> listar() {
        return corredorVialRepository.findAll();
    }
}
