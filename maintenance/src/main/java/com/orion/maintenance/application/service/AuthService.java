package com.orion.maintenance.application.service;

import com.orion.maintenance.application.port.TokenIssuer;
import com.orion.maintenance.application.port.UsuarioRepository;
import com.orion.maintenance.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String CREDENCIALES_INVALIDAS = "Credenciales inválidas";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenIssuer tokenIssuer;

    public AuthResult login(String email, String rawPassword) {
        Usuario usuario =
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new BadCredentialsException(CREDENCIALES_INVALIDAS));

        if (!passwordEncoder.matches(rawPassword, usuario.getPasswordHash())) {
            throw new BadCredentialsException(CREDENCIALES_INVALIDAS);
        }

        String token = tokenIssuer.issue(usuario.getEmail(), usuario.getRol().name());
        return new AuthResult(token, usuario.getNombre(), usuario.getEmail(), usuario.getRol());
    }
}
