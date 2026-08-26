package com.orion.maintenance.application.port;

import com.orion.maintenance.domain.model.Cuadrilla;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuadrillaRepository extends JpaRepository<Cuadrilla, Long> {

    boolean existsByCodigo(String codigo);
}
