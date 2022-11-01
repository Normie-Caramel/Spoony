package com.mobcomp.spoony;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;
import java.util.LinkedList;

/**
 * This activity defines the page where the player was first shown the target question
 * before he/she can answer with compass
 */
public class QuestionActivity extends SpoonyActivity {

    private int questionDisplay;
    private int giveToDisplay;

    private Question question;
    FirebaseHandler fb = new FirebaseHandler();
    LinkedList<Question> questions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fetch layout ids
        questionDisplay = R.layout.askq;
        giveToDisplay = R.layout.text;

        // populate question
        questions = new LinkedList<>();
        fb.updateQuestions(fb.loadQuestionFromJSONFile(this, fb.isFirstBoot()), success -> {
            if (success) {
                questions = fb.getQuestions();
                fb.saveQuestionToJSONFile(this, questions);
                getGameDetails().setQuestions(questions);
                Log.d("GDQ", String.valueOf(questions));
                // fetch question
                question = getGameDetails().newQuestion();
            } else {
                Log.e("GDQERR", "SOMETHING VERY WRONG HAS HAPPENED WITH ADDING QUESTIONS OH GOD");
            }
        });

        // safety net
        if (questions.isEmpty()) {
            questions = getBackupQuestions();
            getGameDetails().setQuestions(questions);
            question = getGameDetails().newQuestion();
        }

        for (Question question : questions) {
            if (question == null) {
                questions = getBackupQuestions();
                getGameDetails().newQuestion();
                break;
            }
        }

        displayGiveToScreen(); // to avoid accidentally showing the question to the wrong player in the first frame
    }

    private LinkedList<Question> getBackupQuestions() {
        Log.e("Question List Error", "Question list is null or contains nulls, defaulting to backup questions.");

        LinkedList<Question> backupQuestions = new LinkedList<>();
        backupQuestions.add(new Question(0, "Who is more like a cat?"));
        backupQuestions.add(new Question(0, "Who is more like a dog?"));
        backupQuestions.add(new Question(0, "Who is more like a horse?"));
        backupQuestions.add(new Question(0, "Who is more like a cow?"));
        backupQuestions.add(new Question(0, "Who is more like a flea?"));
        backupQuestions.add(new Question(0, "Who is more like a fetid little sewer rat?"));
        Collections.shuffle(backupQuestions);
        return backupQuestions;
    }


    @Override
    protected void onEnterLeadView() {
        displayQuestionScreen();
    }

    @Override
    protected void onExitLeadView() {
        displayGiveToScreen();
    }

    private void displayGiveToScreen() {
        setContentView(giveToDisplay);
        commonBtnSetup();
        TextView giveToText = findViewById(R.id.txt);
        giveToText.setText(String.format("Give the phone to %s, silly!",
                getGameDetails().getLead().getName()));
    }

    private void displayQuestionScreen() {
        setContentView(questionDisplay);

        Button button = findViewById(R.id.askq_next_button);
        button.setOnClickListener(this::lockIn);
        commonBtnSetup();

        TextView introText = findViewById(R.id.askq_prompt_txt);
        TextView questionText = findViewById(R.id.askq_question_txt);
        TextView warningText = findViewById(R.id.askq_no_tell_txt);

        introText.setText(String.format("%s, this is your question:",
                getGameDetails().getLead().getName()));
        warningText.setText(String.format("Don't tell %s, and don't forget the question!",
                getGameDetails().getFollow().getName()));

        questionText.setText(question.text);
    }

    // continue to the answer stage
    private void lockIn(View view) {
        changeActivity(AnswerActivity.class, getGameDetails());
    }
}
