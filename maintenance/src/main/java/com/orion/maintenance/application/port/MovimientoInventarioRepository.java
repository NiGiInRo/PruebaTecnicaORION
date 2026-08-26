package com.orion.maintenance.application.port;

import com.orion.maintenance.domain.model.MovimientoInventario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByMaterialIdOrderByFechaDesc(Long materialId);
}
