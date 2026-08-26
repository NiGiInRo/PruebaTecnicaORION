package com.orion.maintenance.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.orion.maintenance.application.port.TokenIssuer;
import com.orion.maintenance.application.port.UsuarioRepository;
import com.orion.maintenance.domain.model.Rol;
import com.orion.maintenance.domain.model.Usuario;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenIssuer tokenIssuer;

    private final AuthService authService() {
        return new AuthService(usuarioRepository, passwordEncoder, tokenIssuer);
    }

    @Test
    void loginExitosoDevuelveTokenYDatosDelUsuario() {
        Usuario usuario =
                new Usuario("Supervisor Demo", "supervisor@orion.com", "hash", Rol.SUPERVISOR);
        when(usuarioRepository.findByEmail("supervisor@orion.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Supervisor123!", "hash")).thenReturn(true);
        when(tokenIssuer.issue("supervisor@orion.com", "SUPERVISOR")).thenReturn("token-generado");

        AuthResult result = authService().login("supervisor@orion.com", "Supervisor123!");

        assertThat(result.token()).isEqualTo("token-generado");
        assertThat(result.nombre()).isEqualTo("Supervisor Demo");
        assertThat(result.rol()).isEqualTo(Rol.SUPERVISOR);
    }

    @Test
    void loginConEmailInexistenteLanzaCredencialesInvalidas() {
        when(usuarioRepository.findByEmail("no-existe@orion.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService().login("no-existe@orion.com", "cualquiera"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginConPasswordIncorrectaLanzaCredencialesInvalidas() {
        Usuario usuario = new Usuario("Tecnico Demo", "tecnico@orion.com", "hash", Rol.TECNICO);
        when(usuarioRepository.findByEmail("tecnico@orion.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("incorrecta", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService().login("tecnico@orion.com", "incorrecta"))
                .isInstanceOf(BadCredentialsException.class);
    }
}
