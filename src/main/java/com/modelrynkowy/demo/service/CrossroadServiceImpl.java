package com.modelrynkowy.demo.service;

import com.modelrynkowy.demo.entity.Crossroad;
import com.modelrynkowy.demo.entity.Position;
import com.modelrynkowy.demo.repository.CrossroadRepository;
import com.modelrynkowy.demo.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static java.lang.StrictMath.abs;

@Service
public class CrossroadServiceImpl implements CrossroadService {
    private CrossroadRepository crossroadRepository;
    private PositionRepository positionRepository;

    @Autowired
    public CrossroadServiceImpl(CrossroadRepository crossroadRepository, PositionRepository positionRepository) {
        this.crossroadRepository = crossroadRepository;
        this.positionRepository = positionRepository;
    }

    public List<Crossroad> getCrossroads() {
        return crossroadRepository.findAll();
    }

    @Override
    public void createCrossroad() {
        Random random = new Random();
        int x = abs(random.nextInt())%1200;
        int y = abs(random.nextInt())%700;
        Position position = positionRepository.save(new Position(x,y));
        crossroadRepository.save(new Crossroad(new HashSet<>(), position));
    }
}
