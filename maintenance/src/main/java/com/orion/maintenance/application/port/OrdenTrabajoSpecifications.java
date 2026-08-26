package com.orion.maintenance.application.port;

import com.orion.maintenance.domain.model.EstadoOrdenTrabajo;
import com.orion.maintenance.domain.model.OrdenTrabajo;
import com.orion.maintenance.domain.model.PrioridadOrdenTrabajo;
import com.orion.maintenance.domain.model.TipoOrdenTrabajo;
import org.springframework.data.jpa.domain.Specification;

public final class OrdenTrabajoSpecifications {

    private OrdenTrabajoSpecifications() {}

    public static Specification<OrdenTrabajo> conActivo(Long activoId) {
        return (root, query, cb) ->
                activoId == null ? null : cb.equal(root.get("activo").get("id"), activoId);
    }

    public static Specification<OrdenTrabajo> conEstado(EstadoOrdenTrabajo estado) {
        return (root, query, cb) -> estado == null ? null : cb.equal(root.get("estado"), estado);
    }

    public static Specification<OrdenTrabajo> conTipo(TipoOrdenTrabajo tipo) {
        return (root, query, cb) -> tipo == null ? null : cb.equal(root.get("tipo"), tipo);
    }

    public static Specification<OrdenTrabajo> conPrioridad(PrioridadOrdenTrabajo prioridad) {
        return (root, query, cb) ->
                prioridad == null ? null : cb.equal(root.get("prioridad"), prioridad);
    }

    public static Specification<OrdenTrabajo> filtros(
            Long activoId, EstadoOrdenTrabajo estado, TipoOrdenTrabajo tipo, PrioridadOrdenTrabajo prioridad) {
        return Specification.where(conActivo(activoId))
                .and(conEstado(estado))
                .and(conTipo(tipo))
                .and(conPrioridad(prioridad));
    }
}
