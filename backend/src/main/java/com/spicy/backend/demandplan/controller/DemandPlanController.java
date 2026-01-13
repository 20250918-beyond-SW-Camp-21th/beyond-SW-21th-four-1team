package com.spicy.backend.demandplan.controller;

import com.spicy.backend.demandplan.controller.dto.response.ProcessResponse;
import com.spicy.backend.demandplan.service.DemandPlanService;
import lombok.RequiredArgsConstructor;
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
    public ProcessResponse checkDemand(@PathVariable Long productId) {
        return service.process(productId);
    }
}
