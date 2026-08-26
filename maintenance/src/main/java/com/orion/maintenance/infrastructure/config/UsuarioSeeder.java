package com.orion.maintenance.infrastructure.config;

import com.orion.maintenance.application.port.UsuarioRepository;
import com.orion.maintenance.domain.model.Rol;
import com.orion.maintenance.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Carga usuarios de demostración (uno por rol) si la tabla usuario está vacía.
 * No hay registro público en este alcance (ver BACKLOG_REFINED.md), así que
 * esta es la única forma de tener credenciales para probar el sistema.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioSeeder implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        crearUsuario("Supervisor Demo", "supervisor@orion.com", "Supervisor123!", Rol.SUPERVISOR);
        crearUsuario("Coordinador Demo", "coordinador@orion.com", "Coordinador123!", Rol.COORDINADOR);
        crearUsuario("Tecnico Demo", "tecnico@orion.com", "Tecnico123!", Rol.TECNICO);

        log.info(
                "Usuarios semilla creados: supervisor@orion.com / coordinador@orion.com /"
                        + " tecnico@orion.com (ver README para las contraseñas)");
    }

    private void crearUsuario(String nombre, String email, String rawPassword, Rol rol) {
        usuarioRepository.save(new Usuario(nombre, email, passwordEncoder.encode(rawPassword), rol));
    }
}
