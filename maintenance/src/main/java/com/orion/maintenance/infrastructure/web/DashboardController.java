package com.orion.maintenance.infrastructure.web;

import com.orion.maintenance.application.service.DashboardService;
import com.orion.maintenance.infrastructure.web.dto.DashboardIndicadoresResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/indicadores")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'COORDINADOR')")
    public DashboardIndicadoresResponse indicadores() {
        return DashboardIndicadoresResponse.from(dashboardService.obtenerIndicadores());
    }
}
