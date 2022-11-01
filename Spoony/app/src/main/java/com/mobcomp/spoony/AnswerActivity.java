package com.mobcomp.spoony;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This activity defines the page where the player can answer the question quantitatively
 * by rotating the phone and play with compass
 */
public class AnswerActivity extends SpoonyActivity {

    private GameDetails gameDetails;

    private int answerDisplay;
    private int putDownDisplay;
    private int promptDisplay;

    private ProgressBar progressBarTop;
    private ProgressBar progressBarBottom;

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
        promptDisplay = R.layout.promptq;
        putDownDisplay = R.layout.down;

        displayPutDownScreen();
    }

    @Override
    protected void onEnterTable() {
        displaySpinnerScreen();
    }

    @Override
    protected void onEnterFollowView() {
        displayPutDownScreen();
    }

    @Override
    protected void onEnterDefault() {
        displayPutDownScreen();
    }

    @Override
    protected void onEnterLeadView() {
        displayPrompt();
    }

    @Override
    protected void updateTable() {
        spinner.setRotation(Angle.rotationDistanceSigned(
                deviceOrientation[0], gameDetails.getLead().getDirection()));

        leadPercent = Math.round(Angle.percentageBetween(
                deviceOrientation[0],
                gameDetails.getFollow().getDirection(),
                gameDetails.getLead().getDirection()) * 100);

        if (leadPercentText != null) {
            leadPercentText.setText(String.format("%s %s%%", gameDetails.getLead().getName(), String.valueOf(leadPercent)));
            followPercentText.setText(String.format("%s %s%%", gameDetails.getFollow().getName(), String.valueOf(100 - leadPercent)));
        }

        gradient.setGradientCenter(0, leadPercent / 100f); // doesn't work on linear gradients >:(

        progressBarTop.setProgress(leadPercent);
        progressBarBottom.setProgress(leadPercent);
    }

    // displays a prompt reminding the player of the question and explaining how to answer it
    private void displayPrompt() {
        setContentView(promptDisplay);

        TextView promptName = findViewById(R.id.prompt_name_txt);
        TextView questionText = findViewById(R.id.prompt_question_txt);

        promptName.setText(String.format("%s, here's your question:", gameDetails.getLead().getName()));
        questionText.setText(gameDetails.getCurrentQuestion().text);
    }

    // displays spinner that points to both players, allowing the lead player to answer the question by turning the phone
    private void displaySpinnerScreen() {
        setContentView(answerDisplay);

        ImageButton lockInButton = findViewById(R.id.lock_button);
        lockInButton.setOnClickListener(this::lockInAnswer);

        spinner = findViewById(R.id.answer_spinner_layout);

        TextView hintText = findViewById(R.id.ansq_txt_hint);

        progressBarTop = findViewById(R.id.ansq_bar_top);
        progressBarBottom = findViewById(R.id.ansq_bar_bottom);

        // set bar colours
        progressBarTop.setProgressTintList(ColorStateList.valueOf(gameDetails.getLead().getColour()));
        progressBarBottom.setProgressTintList(ColorStateList.valueOf(gameDetails.getLead().getColour()));
        progressBarTop.setBackgroundColor(gameDetails.getFollow().getColour());
        progressBarBottom.setBackgroundColor(gameDetails.getFollow().getColour());

        leadPercentText = findViewById(R.id.answer_lead_pct);
        followPercentText = findViewById(R.id.answer_follow_pct);

        leadPercentText.setTextColor(gameDetails.getLead().getColour());
        followPercentText.setTextColor(gameDetails.getFollow().getColour());
        hintText.setText(String.format("%s, turn the phone to answer the question you were just asked.", gameDetails.getLead().getName()));

        int[] gradientColours = {gameDetails.getFollow().getColour(), gameDetails.getLead().getColour()};

        gradient = (GradientDrawable) ResourcesCompat.getDrawable(
                getResources(), R.drawable.circle_gradient, getTheme());

        if (gradient != null) {
            gradient.setColors(gradientColours);
        }
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

        Log.d("AnswerActivity.lockInAnswer", String.format(
                "Question: %s; Answer: %d percent %s",
                gameDetails.getCurrentQuestion().text,
                gameDetails.getCurrentQuestion().percentage,
                gameDetails.getCurrentQuestion().answer.getName()));

        changeActivity(WhichQuestion.class, gameDetails);
    }
}
