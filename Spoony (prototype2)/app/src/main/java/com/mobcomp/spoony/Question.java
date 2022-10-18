package com.mobcomp.spoony;

import java.io.Serializable;

public class Question implements Serializable {
    public int id;
    public String text;
    public Player answer;
    public int percentage;

    public Question(int id, String text) {
        this.id = id;
        this.text = text;
        answer = null;
    }
}
