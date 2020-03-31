package com.modelrynkowy.demo.repository;

import com.modelrynkowy.demo.entity.Crossroad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrossroadRepository extends CrudRepository<Crossroad, Long> {
    Crossroad save(Crossroad crossroad);

    List<Crossroad> findAll();
}