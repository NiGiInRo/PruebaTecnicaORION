package com.orion.maintenance.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.orion.maintenance.application.exception.CodigoDuplicadoException;
import com.orion.maintenance.application.exception.OperacionInvalidaException;
import com.orion.maintenance.application.port.CuadrillaRepository;
import com.orion.maintenance.application.port.CuadrillaTecnicoRepository;
import com.orion.maintenance.application.port.UsuarioRepository;
import com.orion.maintenance.domain.model.Cuadrilla;
import com.orion.maintenance.domain.model.EspecialidadCuadrilla;
import com.orion.maintenance.domain.model.EstadoCuadrilla;
import com.orion.maintenance.domain.model.Rol;
import com.orion.maintenance.domain.model.Usuario;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CuadrillaServiceTest {

    @Mock private CuadrillaRepository cuadrillaRepository;
    @Mock private CuadrillaTecnicoRepository cuadrillaTecnicoRepository;
    @Mock private UsuarioRepository usuarioRepository;

    private CuadrillaService service() {
        return new CuadrillaService(cuadrillaRepository, cuadrillaTecnicoRepository, usuarioRepository);
    }

    private Cuadrilla cuadrillaExistente() {
        Cuadrilla cuadrilla = new Cuadrilla("CUA-01", "Cuadrilla Norte", EspecialidadCuadrilla.ELECTRICA);
        ReflectionTestUtils.setField(cuadrilla, "id", 1L);
        return cuadrilla;
    }

    private Usuario tecnico() {
        Usuario usuario = new Usuario("Tecnico Demo", "tecnico@orion.com", "hash", Rol.TECNICO);
        ReflectionTestUtils.setField(usuario, "id", 5L);
        return usuario;
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

    @Test
    void agregarTecnicoFuncionaSiTieneRolTecnicoYNoPerteneceAOtraCuadrilla() {
        Cuadrilla cuadrilla = cuadrillaExistente();
        Usuario tecnico = tecnico();
        when(cuadrillaRepository.findById(1L)).thenReturn(Optional.of(cuadrilla));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(tecnico));
        when(cuadrillaTecnicoRepository.existsByUsuarioId(5L)).thenReturn(false);
        when(cuadrillaRepository.save(any(Cuadrilla.class))).thenAnswer(inv -> inv.getArgument(0));

        Cuadrilla resultado = service().agregarTecnico(1L, 5L);

        assertThat(resultado.getTecnicos()).hasSize(1);
    }

    @Test
    void noPermiteAgregarUnUsuarioQueNoEsTecnico() {
        Cuadrilla cuadrilla = cuadrillaExistente();
        Usuario supervisor = new Usuario("Supervisor Demo", "sup@orion.com", "hash", Rol.SUPERVISOR);
        ReflectionTestUtils.setField(supervisor, "id", 9L);
        when(cuadrillaRepository.findById(1L)).thenReturn(Optional.of(cuadrilla));
        when(usuarioRepository.findById(9L)).thenReturn(Optional.of(supervisor));

        assertThatThrownBy(() -> service().agregarTecnico(1L, 9L))
                .isInstanceOf(OperacionInvalidaException.class);
    }

    @Test
    void noPermiteAgregarUnTecnicoQueYaPerteneceAOtraCuadrilla() {
        Cuadrilla cuadrilla = cuadrillaExistente();
        Usuario tecnico = tecnico();
        when(cuadrillaRepository.findById(1L)).thenReturn(Optional.of(cuadrilla));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(tecnico));
        when(cuadrillaTecnicoRepository.existsByUsuarioId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service().agregarTecnico(1L, 5L))
                .isInstanceOf(OperacionInvalidaException.class);
    }

    @Test
    void desactivarUnaCuadrillaEnMisionFalla() {
        Cuadrilla cuadrilla = cuadrillaExistente();
        cuadrilla.marcarEnMision();
        when(cuadrillaRepository.findById(1L)).thenReturn(Optional.of(cuadrilla));

        assertThatThrownBy(() -> service().desactivar(1L))
                .isInstanceOf(com.orion.maintenance.domain.exception.TransicionInvalidaException.class);
    }
}
