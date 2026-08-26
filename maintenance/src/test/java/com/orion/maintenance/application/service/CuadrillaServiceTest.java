package com.orion.maintenance.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.orion.maintenance.application.exception.CodigoDuplicadoException;
import com.orion.maintenance.application.port.CuadrillaRepository;
import com.orion.maintenance.domain.model.Cuadrilla;
import com.orion.maintenance.domain.model.EspecialidadCuadrilla;
import com.orion.maintenance.domain.model.EstadoCuadrilla;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CuadrillaServiceTest {

    @Mock private CuadrillaRepository cuadrillaRepository;

    private CuadrillaService service() {
        return new CuadrillaService(cuadrillaRepository);
    }

    @Test
    void creaUnaCuadrillaDisponiblePorDefecto() {
        when(cuadrillaRepository.existsByCodigo("CUA-01")).thenReturn(false);
        when(cuadrillaRepository.save(any(Cuadrilla.class))).thenAnswer(inv -> inv.getArgument(0));

        Cuadrilla cuadrilla = service().crear("CUA-01", "Cuadrilla Norte", EspecialidadCuadrilla.ELECTRICA);

        assertThat(cuadrilla.getEstado()).isEqualTo(EstadoCuadrilla.DISPONIBLE);
    }

    @Test
    void noPermiteCrearUnaCuadrillaConCodigoDuplicado() {
        when(cuadrillaRepository.existsByCodigo("CUA-01")).thenReturn(true);

        assertThatThrownBy(
                        () -> service().crear("CUA-01", "Cuadrilla Norte", EspecialidadCuadrilla.ELECTRICA))
                .isInstanceOf(CodigoDuplicadoException.class);
    }
}
