package com.orion.maintenance.application.port;

import com.orion.maintenance.domain.model.EstadoOrdenTrabajo;
import com.orion.maintenance.domain.model.OrdenTrabajo;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrdenTrabajoRepository
        extends JpaRepository<OrdenTrabajo, Long>, JpaSpecificationExecutor<OrdenTrabajo> {

    boolean existsByActivoIdAndEstadoNotIn(Long activoId, Collection<EstadoOrdenTrabajo> estados);
}
