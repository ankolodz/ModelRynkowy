package com.modelrynkowy.demo.repository;

import com.modelrynkowy.demo.entity.Road;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoadRepository extends CrudRepository<Road, Long> {
    Road save(Road road);

    Optional<Road> findById(int id);
}