package com.orion.maintenance.application.service;

import com.orion.maintenance.application.exception.CodigoDuplicadoException;
import com.orion.maintenance.application.exception.OperacionInvalidaException;
import com.orion.maintenance.application.exception.RecursoNoEncontradoException;
import com.orion.maintenance.application.port.CuadrillaRepository;
import com.orion.maintenance.application.port.CuadrillaTecnicoRepository;
import com.orion.maintenance.application.port.UsuarioRepository;
import com.orion.maintenance.domain.model.Cuadrilla;
import com.orion.maintenance.domain.model.EspecialidadCuadrilla;
import com.orion.maintenance.domain.model.Rol;
import com.orion.maintenance.domain.model.Usuario;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CuadrillaService {

    private final CuadrillaRepository cuadrillaRepository;
    private final CuadrillaTecnicoRepository cuadrillaTecnicoRepository;
    private final UsuarioRepository usuarioRepository;

    public Cuadrilla crear(String codigo, String nombre, EspecialidadCuadrilla especialidad) {
        if (cuadrillaRepository.existsByCodigo(codigo)) {
            throw new CodigoDuplicadoException("Ya existe una cuadrilla con código " + codigo);
        }
        return cuadrillaRepository.save(new Cuadrilla(codigo, nombre, especialidad));
    }

    public Cuadrilla agregarTecnico(Long cuadrillaId, Long usuarioId) {
        Cuadrilla cuadrilla = obtenerPorId(cuadrillaId);
        Usuario usuario = obtenerUsuario(usuarioId);

        if (usuario.getRol() != Rol.TECNICO) {
            throw new OperacionInvalidaException("El usuario " + usuarioId + " no tiene rol TECNICO");
        }
        if (cuadrillaTecnicoRepository.existsByUsuarioId(usuarioId)) {
            throw new OperacionInvalidaException(
                    "El técnico " + usuarioId + " ya pertenece a una cuadrilla");
        }

        cuadrilla.agregarTecnico(usuario);
        return cuadrillaRepository.save(cuadrilla);
    }

    public Cuadrilla quitarTecnico(Long cuadrillaId, Long usuarioId) {
        Cuadrilla cuadrilla = obtenerPorId(cuadrillaId);
        cuadrilla.quitarTecnico(usuarioId);
        return cuadrillaRepository.save(cuadrilla);
    }

    public Cuadrilla designarLider(Long cuadrillaId, Long usuarioId) {
        Cuadrilla cuadrilla = obtenerPorId(cuadrillaId);
        Usuario usuario = obtenerUsuario(usuarioId);
        cuadrilla.designarLider(usuario);
        return cuadrillaRepository.save(cuadrilla);
    }

    public Cuadrilla activar(Long cuadrillaId) {
        Cuadrilla cuadrilla = obtenerPorId(cuadrillaId);
        cuadrilla.activar();
        return cuadrillaRepository.save(cuadrilla);
    }

    public Cuadrilla desactivar(Long cuadrillaId) {
        Cuadrilla cuadrilla = obtenerPorId(cuadrillaId);
        cuadrilla.desactivar();
        return cuadrillaRepository.save(cuadrilla);
    }

    @Transactional(readOnly = true)
    public Cuadrilla obtenerPorId(Long id) {
        return cuadrillaRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuadrilla no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cuadrilla> listar() {
        return cuadrillaRepository.findAll();
    }

    private Usuario obtenerUsuario(Long usuarioId) {
        return usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + usuarioId));
    }
}
