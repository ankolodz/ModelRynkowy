package com.modelrynkowy.demo.service;

import com.modelrynkowy.demo.entity.Crossroad;
import com.modelrynkowy.demo.entity.Road;
import com.modelrynkowy.demo.repository.RoadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoadServiceImpl implements RoadService {
    private RoadRepository roadRepository;
    private CrossroadService crossroadService;

    @Autowired
    public RoadServiceImpl(RoadRepository roadRepository, CrossroadService crossroadService) {
        this.roadRepository = roadRepository;
        this.crossroadService = crossroadService;
    }

    @Override
    public Set<Road> getRoads() {
        return null;
    }

    @Override
    public int getRoadLength(int id) {
        Optional<Road> road = roadRepository.findById(id);

        return road.map(Road::getLength).orElse(0);
    }

    @Override
    public void createRoad() {
        List<Crossroad> crossroads = crossroadService.getCrossroads();
        Road road = new Road(crossroads.get(0), crossroads.get(1));

        roadRepository.save(road);
    }
}
