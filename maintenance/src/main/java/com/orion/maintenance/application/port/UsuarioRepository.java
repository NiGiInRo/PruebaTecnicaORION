package com.orion.maintenance.application.port;

import com.orion.maintenance.domain.model.Rol;
import com.orion.maintenance.domain.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByRol(Rol rol);
}
