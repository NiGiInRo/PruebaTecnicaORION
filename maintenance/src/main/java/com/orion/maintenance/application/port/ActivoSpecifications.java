package com.orion.maintenance.application.port;

import com.orion.maintenance.domain.model.Activo;
import com.orion.maintenance.domain.model.EstadoActivo;
import com.orion.maintenance.domain.model.TipoActivo;
import org.springframework.data.jpa.domain.Specification;

/** Filtros dinámicos para {@link ActivoRepository}. Vive en application (no infrastructure) porque
 * ActivoRepository ya expone Specification en su firma (JpaSpecificationExecutor, ver ADR-8). */
public final class ActivoSpecifications {

    private ActivoSpecifications() {}

    public static Specification<Activo> conCorredor(Long corredorId) {
        return (root, query, cb) ->
                corredorId == null ? null : cb.equal(root.get("corredor").get("id"), corredorId);
    }

    public static Specification<Activo> conTipo(TipoActivo tipo) {
        return (root, query, cb) -> tipo == null ? null : cb.equal(root.get("tipo"), tipo);
    }

    public static Specification<Activo> conEstado(EstadoActivo estado) {
        return (root, query, cb) -> estado == null ? null : cb.equal(root.get("estado"), estado);
    }

    public static Specification<Activo> filtros(Long corredorId, TipoActivo tipo, EstadoActivo estado) {
        return Specification.where(conCorredor(corredorId)).and(conTipo(tipo)).and(conEstado(estado));
    }
}
