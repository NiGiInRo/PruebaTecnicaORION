package com.orion.maintenance.application.service;

import com.orion.maintenance.application.port.UsuarioRepository;
import com.orion.maintenance.domain.model.Rol;
import com.orion.maintenance.domain.model.Usuario;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<Usuario> listar(Rol rol) {
        return rol == null ? usuarioRepository.findAll() : usuarioRepository.findByRol(rol);
    }
}
