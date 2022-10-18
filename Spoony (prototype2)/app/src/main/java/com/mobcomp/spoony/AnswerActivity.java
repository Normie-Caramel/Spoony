package com.mobcomp.spoony;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class AnswerActivity extends SpoonyActivity {

    private GameDetails gameDetails;

    private int answerDisplay;
    private int putDownDisplay;

    private ConstraintLayout spinner;
    private GradientDrawable gradient;

    private int leadPercent;
    private TextView leadPercentText;
    private TextView followPercentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ansq);

        gameDetails = getGameDetails();

        answerDisplay = R.layout.ansq;
        putDownDisplay = R.layout.down;

        displayPutDownScreen();
    }

    @Override
    protected void onEnterTable() {
        displaySpinnerScreen();
    }

    @Override
    protected void onExitTable() {
        displayPutDownScreen();
    }

    @Override
    protected void updateTable() {
        spinner.setRotation(Angle.rotationDistanceSigned(deviceOrientation[0], gameDetails.getLead().getDirection()));

        leadPercent = Math.round(Angle.percentageBetween(deviceOrientation[0], gameDetails.getFollow().getDirection(), gameDetails.getLead().getDirection()) * 100);

        if (leadPercentText != null) {
            leadPercentText.setText(String.valueOf(leadPercent));
            followPercentText.setText(String.valueOf((100 - leadPercent)));
        }
    }

    private void displaySpinnerScreen() {
        setContentView(answerDisplay);

        ImageButton lockInButton = findViewById(R.id.lock_button);
        lockInButton.setOnClickListener(this::lockInAnswer);

        TextView leadText = findViewById(R.id.answer_lead_text);
        TextView followText = findViewById(R.id.answer_follow_text);

        spinner = findViewById(R.id.answer_spinner_layout);

        leadText.setTextColor(gameDetails.getLead().getColour());
        leadText.setText(gameDetails.getLead().getName());

        followText.setTextColor(gameDetails.getFollow().getColour());
        followText.setText(gameDetails.getFollow().getName());

        leadPercentText = findViewById(R.id.answer_lead_pct);
        followPercentText = findViewById(R.id.answer_follow_pct);
    }

    private void displayPutDownScreen() {
        setContentView(putDownDisplay);
    }

    private void lockInAnswer(View view) {
        if (leadPercent >= 50) {
            gameDetails.getCurrentQuestion().answer = gameDetails.getLead();
            gameDetails.getCurrentQuestion().percentage = leadPercent;
        }

        else {
            gameDetails.getCurrentQuestion().answer = gameDetails.getFollow();
            gameDetails.getCurrentQuestion().percentage = 100 - leadPercent;
        }

        Log.d("AnswerActivity.lockInAnswer", String.format("Question: %s; Answer: %d percent %s",
                gameDetails.getCurrentQuestion().text,
                gameDetails.getCurrentQuestion().percentage,
                gameDetails.getCurrentQuestion().answer.getName()));

        changeActivity(WhichQuestion.class, gameDetails);
    }
}
