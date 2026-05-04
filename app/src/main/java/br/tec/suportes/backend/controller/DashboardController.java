package br.tec.suportes.backend.controller;

import br.tec.suportes.backend.dto.ApiResponse;
import br.tec.suportes.backend.dto.dashboard.DashboardDTO;
import br.tec.suportes.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardDTO>> resumo() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.resumo()));
    }
}
