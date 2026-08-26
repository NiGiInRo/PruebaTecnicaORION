package com.orion.maintenance.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private final JwtService jwtService =
            new JwtService("test-secret-key-must-be-long-enough-for-hs256-1234567890", 60_000);

    @Test
    void generaUnTokenQueContieneElSubjectYElRol() {
        String token = jwtService.generateToken("supervisor@orion.com", "SUPERVISOR");

        Claims claims = jwtService.parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("supervisor@orion.com");
        assertThat(jwtService.extractRol(claims)).isEqualTo("SUPERVISOR");
    }

    @Test
    void unTokenExpiradoNoPasaLaValidacion() {
        JwtService jwtServiceExpiraInstantaneo =
                new JwtService("test-secret-key-must-be-long-enough-for-hs256-1234567890", -1_000);

        String token = jwtServiceExpiraInstantaneo.generateToken("tecnico@orion.com", "TECNICO");

        assertThatThrownBy(() -> jwtServiceExpiraInstantaneo.parseClaims(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
