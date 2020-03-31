package com.modelrynkowy.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "road")
public class Road {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(targetEntity = Crossroad.class)
    private Crossroad start;

    @OneToOne(targetEntity = Crossroad.class)
    private Crossroad end;

    public Road(Crossroad start, Crossroad end) {
        this.start = start;
        this.end = end;
    }

    public int getLength() {
        Position startPosition = start.getPosition();
        Position endPosition = end.getPosition();

        return (int) Math.sqrt(Math.pow(startPosition.getPositionX() - endPosition.getPositionX(), 2) + Math.pow(startPosition.getPositionY() - endPosition.getPositionY(), 2));
    }
}
