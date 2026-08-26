package com.orion.maintenance.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.orion.maintenance.application.exception.CodigoDuplicadoException;
import com.orion.maintenance.application.exception.RecursoNoEncontradoException;
import com.orion.maintenance.application.port.CorredorVialRepository;
import com.orion.maintenance.domain.model.CorredorVial;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CorredorVialServiceTest {

    @Mock private CorredorVialRepository corredorVialRepository;

    private CorredorVialService service() {
        return new CorredorVialService(corredorVialRepository);
    }

    @Test
    void creaUnCorredorCuandoElCodigoNoExiste() {
        when(corredorVialRepository.existsByCodigo("CV-01")).thenReturn(false);
        when(corredorVialRepository.save(any(CorredorVial.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CorredorVial resultado = service().crear("CV-01", "Autopista Norte", "Tramo 1");

        assertThat(resultado.getCodigo()).isEqualTo("CV-01");
        assertThat(resultado.getNombre()).isEqualTo("Autopista Norte");
    }

    @Test
    void noPermiteCrearUnCorredorConCodigoDuplicado() {
        when(corredorVialRepository.existsByCodigo("CV-01")).thenReturn(true);

        assertThatThrownBy(() -> service().crear("CV-01", "Autopista Norte", "Tramo 1"))
                .isInstanceOf(CodigoDuplicadoException.class);
    }

    @Test
    void obtenerPorIdLanzaExcepcionSiNoExiste() {
        when(corredorVialRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
