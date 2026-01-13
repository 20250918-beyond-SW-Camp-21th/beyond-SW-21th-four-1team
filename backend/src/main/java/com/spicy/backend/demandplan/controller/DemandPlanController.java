package com.spicy.backend.demandplan.controller;

import com.spicy.backend.demandplan.controller.dto.response.ProcessResponse;
import com.spicy.backend.demandplan.service.DemandPlanService;
import com.spicy.backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demand-plan")
@RequiredArgsConstructor
public class DemandPlanController {

    private final DemandPlanService service;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProcessResponse>> checkDemand(@PathVariable Long productId) {

        return ResponseEntity.ok(ApiResponse.success(service.process(productId)));
    }
}
