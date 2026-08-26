package com.orion.maintenance.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.orion.maintenance.application.exception.RecursoNoEncontradoException;
import com.orion.maintenance.application.port.MovimientoInventarioRepository;
import com.orion.maintenance.application.port.OrdenTrabajoRepository;
import com.orion.maintenance.application.port.UsuarioRepository;
import com.orion.maintenance.domain.exception.StockInsuficienteException;
import com.orion.maintenance.domain.model.Activo;
import com.orion.maintenance.domain.model.CorredorVial;
import com.orion.maintenance.domain.model.Material;
import com.orion.maintenance.domain.model.MovimientoInventario;
import com.orion.maintenance.domain.model.OrdenTrabajo;
import com.orion.maintenance.domain.model.OrigenOrdenTrabajo;
import com.orion.maintenance.domain.model.PrioridadOrdenTrabajo;
import com.orion.maintenance.domain.model.Rol;
import com.orion.maintenance.domain.model.TipoActivo;
import com.orion.maintenance.domain.model.TipoOrdenTrabajo;
import com.orion.maintenance.domain.model.Usuario;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock private MaterialService materialService;
    @Mock private MovimientoInventarioRepository movimientoInventarioRepository;
    @Mock private OrdenTrabajoRepository ordenTrabajoRepository;
    @Mock private UsuarioRepository usuarioRepository;

    private InventarioService service() {
        return new InventarioService(
                materialService, movimientoInventarioRepository, ordenTrabajoRepository, usuarioRepository);
    }

    private Material material() {
        return new Material("MAT-01", "Lámpara LED PMV", "unidad", new BigDecimal("5"));
    }

    private Usuario supervisor() {
        return new Usuario("Supervisor Demo", "supervisor@orion.com", "hash", Rol.SUPERVISOR);
    }

    private OrdenTrabajo ot() {
        CorredorVial corredor = new CorredorVial("CV-01", "Autopista Norte", "Tramo 1");
        Activo activo = new Activo("PMV-01", "Panel Norte", TipoActivo.PMV, corredor, null, null, null, null);
        return new OrdenTrabajo(
                activo,
                TipoOrdenTrabajo.CORRECTIVO,
                PrioridadOrdenTrabajo.ALTA,
                "Falla",
                null,
                OrigenOrdenTrabajo.MANUAL,
                supervisor());
    }

    @Test
    void registrarEntradaAumentaElStockYQuedaComoMovimientoDeEntrada() {
        Material material = material();
        when(materialService.obtenerPorId(1L)).thenReturn(material);
        when(usuarioRepository.findByEmail("supervisor@orion.com")).thenReturn(Optional.of(supervisor()));
        when(movimientoInventarioRepository.save(any(MovimientoInventario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        MovimientoInventario mov = service().registrarEntrada(1L, new BigDecimal("10"), "supervisor@orion.com");

        assertThat(material.getStockActual()).isEqualByComparingTo(new BigDecimal("10"));
        assertThat(mov.getOrdenTrabajo()).isNull();
    }

    @Test
    void registrarConsumoDisminuyeElStockYAsociaLaOt() {
        Material material = material();
        material.registrarEntrada(new BigDecimal("10"));
        OrdenTrabajo ot = ot();
        when(materialService.obtenerPorId(1L)).thenReturn(material);
        when(ordenTrabajoRepository.findById(5L)).thenReturn(Optional.of(ot));
        when(usuarioRepository.findByEmail("tecnico@orion.com")).thenReturn(Optional.of(supervisor()));
        when(movimientoInventarioRepository.save(any(MovimientoInventario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        MovimientoInventario mov =
                service().registrarConsumo(1L, 5L, new BigDecimal("3"), "tecnico@orion.com");

        assertThat(material.getStockActual()).isEqualByComparingTo(new BigDecimal("7"));
        assertThat(mov.getOrdenTrabajo()).isEqualTo(ot);
    }

    @Test
    void noPermiteRegistrarConsumoMayorAlStockDisponible() {
        Material material = material();
        material.registrarEntrada(new BigDecimal("2"));
        when(materialService.obtenerPorId(1L)).thenReturn(material);
        when(ordenTrabajoRepository.findById(5L)).thenReturn(Optional.of(ot()));
        when(usuarioRepository.findByEmail("tecnico@orion.com")).thenReturn(Optional.of(supervisor()));

        assertThatThrownBy(() -> service().registrarConsumo(1L, 5L, new BigDecimal("3"), "tecnico@orion.com"))
                .isInstanceOf(StockInsuficienteException.class);
    }

    @Test
    void noPermiteRegistrarConsumoParaUnaOtInexistente() {
        Material material = material();
        material.registrarEntrada(new BigDecimal("10"));
        when(materialService.obtenerPorId(1L)).thenReturn(material);
        when(ordenTrabajoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().registrarConsumo(1L, 99L, new BigDecimal("1"), "tecnico@orion.com"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
