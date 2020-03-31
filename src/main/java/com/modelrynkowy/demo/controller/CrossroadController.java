package com.modelrynkowy.demo.controller;

import com.modelrynkowy.demo.entity.Crossroad;
import com.modelrynkowy.demo.service.CrossroadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/crossroads")
@CrossOrigin(origins = "*")
public class CrossroadController {
    private CrossroadService crossroadService;

    @Autowired
    public CrossroadController(CrossroadService crossroadService) {
        this.crossroadService = crossroadService;
    }

    @GetMapping
    public List<Crossroad> getCrossroads() {
        return crossroadService.getCrossroads();
    }

    @GetMapping(value="/mock")
    public void mockCrossroads() {
        for(int i=0; i<10; ++i) {
            crossroadService.createCrossroad();
        }
    }

}