package com.mobcomp.spoony;

import java.io.Serializable;

public class Question implements Serializable {
    public int id;
    public String question;
    public Player answer;
    public int percentage;

    public Question(int id, String question) {
        this.id = id;
        this.question = question;
        answer = null;
    }
}
