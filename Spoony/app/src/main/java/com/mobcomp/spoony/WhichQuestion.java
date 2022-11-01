package com.mobcomp.spoony;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This activity define the page that prompt 3 options for player to guess
 */
public class WhichQuestion extends SpoonyActivity {

    private GameDetails gd;
    private TextView text_txt_center;
    private TextView whichq_txt_leadName;
    private Button whichq_btn_OptA;
    private Button whichq_btn_OptB;
    private Button whichq_btn_OptC;
    private TextView whichq_txt_leadScore;
    private TextView whichq_txt_followScore;
    private TextView whichq_txt_leadScoreName;
    private TextView whichq_txt_followScoreName;
    private ArrayList<String> questionSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gd = getGameDetails();

        // Fetch and shuffle questions
        questionSet = new ArrayList<>();
        String q = gd.getCurrentQuestion().text;
        gd.setCurrentString(q);
        questionSet.add(q);
        questionSet.add(gd.getQuestionNonDestructive().text);
        questionSet.add(gd.getQuestionNonDestructive().text);
        Collections.shuffle(questionSet);

    }

    private void optionClick(View view) {

        // Identify which button was clicked:
        Button btnClicked = (Button) view;

        // Set the 'guessed' question based on button clicked:
        if (btnClicked.getId()==whichq_btn_OptA.getId()) {
            gd.setGuessedString(questionSet.get(0));
        } else if (btnClicked.getId()==whichq_btn_OptB.getId()) {
            gd.setGuessedString(questionSet.get(1));
        } else {
            gd.setGuessedString(questionSet.get(2));
        }

        // Start next activity:
        Intent intent = new Intent(this, WhichAnswer.class);
        intent.putExtra("GameDetails", gd);
        startActivity(intent);

    }

    @SuppressLint("SetTextI18n")
    protected void onEnterLeadView() {
        setContentView(R.layout.text);
        text_txt_center = findViewById(R.id.txt);
        text_txt_center.setText(gd.getLead().getName() + ", give me to " + gd.getFollow().getName());
    }

    protected void onEnterFollowView() {
        displayQuestions();
    }

    @SuppressLint("SetTextI18n")
    protected void onEnterTable() {
        displayQuestions();
    }

    private void displayQuestions() {
        // Construct view:
        setContentView(R.layout.whichq);
        whichq_txt_leadName = findViewById(R.id.whichq_txt_leadName);
        whichq_txt_leadName.setText(gd.getLead().getName());
        whichq_txt_leadName.setTextColor(gd.getLead().getColour());
        whichq_btn_OptA = findViewById(R.id.whichq_btn_OptA);
        whichq_btn_OptB = findViewById(R.id.whichq_btn_OptB);
        whichq_btn_OptC = findViewById(R.id.whichq_btn_OptC);
        View.OnClickListener optionClick = this::optionClick;
        whichq_btn_OptA.setOnClickListener(optionClick);
        whichq_btn_OptB.setOnClickListener(optionClick);
        whichq_btn_OptC.setOnClickListener(optionClick);

        // Set question text in buttons:
        whichq_btn_OptA.setText( questionSet.get(0));
        whichq_btn_OptB.setText( questionSet.get(1));
        whichq_btn_OptC.setText( questionSet.get(2));

        // Set answer display
        whichq_txt_leadScore = findViewById(R.id.whichq_txt_leadScore);
        whichq_txt_followScore = findViewById(R.id.whichq_txt_followScore);
        whichq_txt_leadScoreName = findViewById(R.id.whichq_txt_leadScoreName);
        whichq_txt_followScoreName = findViewById(R.id.whichq_txt_followScoreName);

        int leadPercent;

        if (gd.getLead() == gd.getCurrentQuestion().answer) {
            leadPercent = gd.getCurrentQuestion().percentage;
        }
        else leadPercent = 100 - gd.getCurrentQuestion().percentage;

        whichq_txt_leadScore.setText(String.format("%d%%", leadPercent));
        whichq_txt_followScore.setText(String.format("%d%%", 100 - leadPercent));
        whichq_txt_leadScoreName.setText(gd.getLead().getName());
        whichq_txt_followScoreName.setText(gd.getFollow().getName());
        whichq_txt_leadScore.setTextColor(gd.getLead().getColour());
        whichq_txt_followScore.setTextColor(gd.getFollow().getColour());
        whichq_txt_leadScoreName.setTextColor(gd.getLead().getColour());
        whichq_txt_followScoreName.setTextColor(gd.getFollow().getColour());
    }
}