package com.mobcomp.spoony;

import android.util.Log;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 * This class records player info and questions. It is carried by intent
 * and passed between activities.
 */
public class GameDetails implements Serializable {

    private LinkedList<Question> freshQuestions;
    private Question currentQuestion;
    private String guessedString;
    private String currentString;
    private Player lead;
    private Player follow;
    private int round;

    public GameDetails() {
        freshQuestions = new LinkedList<>();
        lead = null;
        follow = null;
    }

    public void addPlayer(Player player, boolean isLead) {
        if (isLead)
            lead = player;
        else
            follow = player;
    }

    public void setQuestions(LinkedList<Question> qs) {
        freshQuestions = qs;
    }

    // retrieves a random new question and removes it from the list
    public Question newQuestion() {
        Collections.shuffle(freshQuestions);
        Log.d("GDQUESTION", String.valueOf(freshQuestions));

        currentQuestion = freshQuestions.pop();
        return currentQuestion;
    }

    // retrieves a random new question without removing it from the list
    public Question getQuestionNonDestructive() {
        Random random = new Random();
        return freshQuestions.get(random.nextInt(freshQuestions.size()));
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setGuessedString(String qString) {
        guessedString = qString;
    }
    public String getGuessedString() {return guessedString;}
    public void setCurrentString(String cString) {currentString = cString;}
    public String getCurrentString() {return currentString;}

    public Player getLead() {
        return lead;
    }

    public Player getFollow() {
        return follow;
    }

    public int getRound() {
        return round;
    }

    public void nextRound() {
        round++;
        Player temp = lead;
        lead = follow;
        follow = temp;
    }

}
