package com.mobcomp.spoony;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.Bundle;

import java.util.Collections;
import java.util.LinkedList;

public class WhichQuestion extends SpoonyActivity {

    private GameDetails gd;
    private TextView text_txt_center;
    private TextView whichq_txt_leadName;
    private TextView txt_OptA;
    private Button whichq_btn_OptA;
    private Button whichq_btn_OptB;
    private Button whichq_btn_OptC;
    private Button whichq_btn_next;
    private LinkedList<Question> questionSet;
    private Drawable drwBtnDefault;
    private Drawable drwBtnClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gd = getGameDetails();

        // fetch and shuffle questions
        questionSet = new LinkedList<Question>();
        questionSet.add(gd.getCurrentQuestion());
        questionSet.add(gd.getQuestionNonDestructive());
        questionSet.add(gd.getQuestionNonDestructive());

        Collections.shuffle(questionSet);

        drwBtnDefault = getResources().getDrawable(R.drawable.question_box_choose);
        drwBtnClicked = getResources().getDrawable(R.drawable.question_box_choose_trans);

    }

    private void optionClick(View view) {
        //whichq_btn_OptA.setBackground(drwBtnDefault);
        whichq_btn_OptB.setBackground(drwBtnDefault);
        whichq_btn_OptC.setBackground(drwBtnDefault);
        Button btnClicked = (Button) view;
        btnClicked.setBackground(drwBtnClicked);
        whichq_btn_next.setClickable(true);
        whichq_btn_next.setTextColor(Color.BLACK);

        if (btnClicked.getId()==whichq_btn_OptA.getId()) {
            gd.setGuessedQuestion(questionSet.get(0));
        } else if (btnClicked.getId()==whichq_btn_OptB.getId()) {
            gd.setGuessedQuestion(questionSet.get(1));
        } else {
            gd.setGuessedQuestion(questionSet.get(2));
        }

    }

    private void nextClick() {
        Intent intent = new Intent(this, WhichAnswer.class);
        intent.putExtra("GameDetails", gd);
        startActivity(intent);
    }

    protected void onEnterLeadView() {
        setContentView(R.layout.text);
        text_txt_center = findViewById(R.id.txt);
        text_txt_center.setText(gd.getLead().getName() + ", give me to " + gd.getFollow().getName());
    }

    protected void updateLeadView() {}
    protected void onExitLeadView() {}

    protected void onEnterFollowView() {
        setContentView(R.layout.whichq);
        whichq_txt_leadName = findViewById(R.id.whichq_txt_leadName);
        whichq_txt_leadName.setText(gd.getLead().getName());
        whichq_txt_leadName.setTextColor(gd.getLead().getColour());
        whichq_btn_OptA = findViewById(R.id.whichq_btn_OptA);
        whichq_btn_OptB = findViewById(R.id.whichq_btn_OptB);
        whichq_btn_OptC = findViewById(R.id.whichq_btn_OptC);
        whichq_btn_next = findViewById(R.id.whichq_btn_next);
        View.OnClickListener optionClick = view -> optionClick(view);
        View.OnClickListener nextClick = view -> nextClick();
        whichq_btn_OptA.setOnClickListener(optionClick);
        whichq_btn_OptB.setOnClickListener(optionClick);
        whichq_btn_OptC.setOnClickListener(optionClick);
        whichq_btn_next.setOnClickListener(nextClick);

        whichq_btn_OptA.setText("A. " + questionSet.get(0).text);
        whichq_btn_OptB.setText("B. " + questionSet.get(1).text);
        whichq_btn_OptC.setText("C. " + questionSet.get(2).text);

    }

    protected void onEnterTable() {
        setContentView(R.layout.text);
        text_txt_center = findViewById(R.id.txt);
        text_txt_center.setText(gd.getFollow().getName() + ", pick me up!");
    }

}