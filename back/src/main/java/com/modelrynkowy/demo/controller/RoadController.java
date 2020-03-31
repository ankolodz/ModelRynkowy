package com.modelrynkowy.demo.controller;

import com.modelrynkowy.demo.entity.Road;
import com.modelrynkowy.demo.service.RoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/roads")
@CrossOrigin(origins = "*")
public class RoadController {
    private RoadService roadService;

    @Autowired
    public RoadController(RoadService roadService) {
        this.roadService = roadService;
    }

    @GetMapping(value = "/")
    public Set<Road> getCrossroads() {
        return roadService.getRoads();
    }

    @GetMapping(value = "/{id}/length")
    public int getRoadLength(@PathVariable(name = "id") int id) {
        return roadService.getRoadLength(id);
    }

    @GetMapping(value = "/mock")
    public void mockCrossroads() {
        roadService.createRoad();
    }
}
