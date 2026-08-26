package com.orion.maintenance.application.service;

import com.orion.maintenance.application.exception.CodigoDuplicadoException;
import com.orion.maintenance.application.exception.RecursoNoEncontradoException;
import com.orion.maintenance.application.port.MaterialRepository;
import com.orion.maintenance.domain.model.Material;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;

    public Material crear(String codigo, String nombre, String unidadMedida, BigDecimal stockMinimo) {
        if (materialRepository.existsByCodigo(codigo)) {
            throw new CodigoDuplicadoException("Ya existe un material con código " + codigo);
        }
        return materialRepository.save(new Material(codigo, nombre, unidadMedida, stockMinimo));
    }

    @Transactional(readOnly = true)
    public Material obtenerPorId(Long id) {
        return materialRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Material no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Material> listar() {
        return materialRepository.findAll();
    }
}
