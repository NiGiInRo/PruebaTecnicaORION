package com.orion.maintenance.application.port;

import com.orion.maintenance.domain.model.CorredorVial;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorredorVialRepository extends JpaRepository<CorredorVial, Long> {

    Optional<CorredorVial> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
