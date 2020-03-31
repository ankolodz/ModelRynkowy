package com.modelrynkowy.demo.service;

import com.modelrynkowy.demo.entity.Road;

import java.util.Set;

public interface RoadService {
    Set<Road> getRoads();

    int getRoadLength(int id);

    void createRoad();
}
