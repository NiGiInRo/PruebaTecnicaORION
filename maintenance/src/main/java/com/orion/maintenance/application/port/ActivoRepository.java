package com.orion.maintenance.application.port;

import com.orion.maintenance.domain.model.Activo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivoRepository extends JpaRepository<Activo, Long>, JpaSpecificationExecutor<Activo> {

    boolean existsByCodigo(String codigo);
}
