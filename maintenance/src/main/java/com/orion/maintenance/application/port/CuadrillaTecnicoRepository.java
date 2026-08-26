package com.orion.maintenance.application.port;

import com.orion.maintenance.domain.model.CuadrillaTecnico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuadrillaTecnicoRepository extends JpaRepository<CuadrillaTecnico, Long> {

    boolean existsByUsuarioId(Long usuarioId);
}
