package com.orion.maintenance.application.port;

import com.orion.maintenance.domain.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    boolean existsByCodigo(String codigo);
}
