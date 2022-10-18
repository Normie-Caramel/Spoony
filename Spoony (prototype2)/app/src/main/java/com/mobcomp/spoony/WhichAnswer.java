package com.mobcomp.spoony;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.Bundle;

public class WhichAnswer extends GameActivity {

    private GameDetails gd;
    private TextView result_txt_leadName;
    private TextView result_txt_question;
    private TextView result_txt_outcome;
    private Button result_btn_home;
    private Button result_btn_playAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        Intent intent = getIntent();
        gd = (GameDetails) intent.getSerializableExtra("GameDetails");

        result_txt_leadName = findViewById(R.id.result_txt_leadName);
        result_txt_question = findViewById(R.id.result_txt_question);
        result_txt_outcome = findViewById(R.id.result_txt_outcome);
        result_btn_home = findViewById(R.id.result_btn_home);
        result_btn_playAgain = findViewById(R.id.result_btn_playAgain);

        result_txt_leadName.setText(gd.getLead().getName());
        result_txt_leadName.setTextColor(gd.getLead().getColour());
        result_txt_question.setText(gd.getCurrentQuestion().text);

        if (gd.getGuessedQuestion() == gd.getCurrentQuestion()) {
            result_txt_outcome.setText("YOU GOT IT!");
        } else {
            result_txt_outcome.setText("MAYBE NEXT TIME");
        }

        result_btn_home = findViewById(R.id.result_btn_home);
        result_btn_playAgain = findViewById(R.id.result_btn_playAgain);
        View.OnClickListener jumpToHome = view -> jumpToHome();
        View.OnClickListener playAgain = view -> playAgain();
        result_btn_home.setOnClickListener(jumpToHome);
        result_btn_playAgain.setOnClickListener(playAgain);

    }

    private void jumpToHome() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("GameDetails", gd);
        startActivity(intent);
    }

    private void playAgain() {
        gd.nextRound();
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra("GameDetails", gd);
        startActivity(intent);
    }
}
