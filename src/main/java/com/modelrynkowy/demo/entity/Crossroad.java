package com.modelrynkowy.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "crossroad")
public class Crossroad {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Road> roads;

    //TODO Make embedded
    @OneToOne(targetEntity = Position.class)
    private Position position;

    @Transient
    private boolean reserved;

    public Crossroad(Set<Road> roads, Position position) {
        this.roads = roads;
        this.position = position;
    }

    public Position getPosition() {
        return this.position;
    }
}
