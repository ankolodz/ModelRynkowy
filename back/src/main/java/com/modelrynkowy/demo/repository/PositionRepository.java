package com.modelrynkowy.demo.repository;

import com.modelrynkowy.demo.entity.Position;
import org.springframework.data.repository.CrudRepository;

public interface PositionRepository extends CrudRepository<Position, Long> {
    Position save(Position position);
}