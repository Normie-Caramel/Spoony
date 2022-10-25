package com.mobcomp.spoony;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private float direction;
    private int colour;
    private int score;

    public Player(String name, int colour){
        this.name = name;
        this.colour = colour;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public int getColour() {
        return colour;
    }

    public int getScore() { return score; }

    public void incrementScore () {
        this.score = score + 1;
    }
}
