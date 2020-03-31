package com.modelrynkowy.demo.service;

import com.modelrynkowy.demo.entity.Crossroad;

import java.util.List;

public interface CrossroadService {
    List<Crossroad> getCrossroads();

    void createCrossroad();
}
