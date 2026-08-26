package com.orion.maintenance.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.orion.maintenance.application.exception.CodigoDuplicadoException;
import com.orion.maintenance.application.exception.RecursoNoEncontradoException;
import com.orion.maintenance.application.port.ActivoRepository;
import com.orion.maintenance.domain.model.Activo;
import com.orion.maintenance.domain.model.CorredorVial;
import com.orion.maintenance.domain.model.EstadoActivo;
import com.orion.maintenance.domain.model.TipoActivo;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivoServiceTest {

    @Mock private ActivoRepository activoRepository;
    @Mock private CorredorVialService corredorVialService;

    private ActivoService service() {
        return new ActivoService(activoRepository, corredorVialService);
    }

    private CorredorVial corredorDePrueba() {
        return new CorredorVial("CV-01", "Autopista Norte", "Tramo 1");
    }

    @Test
    void creaUnActivoOperativoPorDefecto() {
        when(activoRepository.existsByCodigo("PMV-01")).thenReturn(false);
        when(corredorVialService.obtenerPorId(1L)).thenReturn(corredorDePrueba());
        when(activoRepository.save(any(Activo.class))).thenAnswer(inv -> inv.getArgument(0));

        Activo activo =
                service()
                        .crear(
                                "PMV-01",
                                "Panel Norte",
                                TipoActivo.PMV,
                                1L,
                                new BigDecimal("34.500"),
                                "Fabricante X",
                                "Modelo Y",
                                LocalDate.of(2024, 1, 1));

        assertThat(activo.getEstado()).isEqualTo(EstadoActivo.OPERATIVO);
        assertThat(activo.getCorredor().getCodigo()).isEqualTo("CV-01");
    }

    @Test
    void noPermiteCrearUnActivoConCodigoDuplicado() {
        when(activoRepository.existsByCodigo("PMV-01")).thenReturn(true);

        assertThatThrownBy(
                        () ->
                                service()
                                        .crear(
                                                "PMV-01",
                                                "Panel Norte",
                                                TipoActivo.PMV,
                                                1L,
                                                null,
                                                null,
                                                null,
                                                null))
                .isInstanceOf(CodigoDuplicadoException.class);
    }

    @Test
    void noPermiteCrearUnActivoConCorredorInexistente() {
        when(activoRepository.existsByCodigo("PMV-01")).thenReturn(false);
        when(corredorVialService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("Corredor vial no encontrado: 99"));

        assertThatThrownBy(
                        () ->
                                service()
                                        .crear(
                                                "PMV-01",
                                                "Panel Norte",
                                                TipoActivo.PMV,
                                                99L,
                                                null,
                                                null,
                                                null,
                                                null))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
