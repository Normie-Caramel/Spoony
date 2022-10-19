package com.mobcomp.spoony;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Name extends GameActivity {

    Button name_btn_next;
    TextView name_txt_getName;
    TextView name_txt_p1p2;
    GameDetails gameDetails;
    ImageButton backBtn;
    ImageView homeBtn;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name);
        commonBtnSetup();

        if (gameDetails == null) {
            Intent intent = getIntent();
            gameDetails = (GameDetails) intent.getSerializableExtra("GameDetails");
        }

        // The textview that display "Spooner 1" or "Spooner 2" to prompt user input
        name_txt_p1p2 = findViewById(R.id.textView_p1p2);
        name_txt_p1p2.setText("Spooner 1");

        // save player names to GameDetails
        name_btn_next = (Button) findViewById(R.id.next_button);
        name_btn_next.setOnClickListener((View v) -> {
            name_txt_getName = findViewById(R.id.name_input);
            String p1Name = name_txt_getName.getText().toString();
            if (p1Name.length() > 0){
                Player player1 = new Player(p1Name, ContextCompat.getColor(this, R.color.p1_color));
                gameDetails.addPlayer(player1);
                Log.d("P1 name saved", p1Name);
                nextPlayerEntry(v);
            } else{
                name_txt_getName.setBackgroundColor(Color.rgb(249,174,174));
            }
        });

    }
    private void commonBtnSetup() {
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        backBtn.setOnClickListener((View v) -> backToHomePage());
        homeBtn = (ImageView) findViewById(R.id.home_btn);
        homeBtn.setOnClickListener((View v) -> backToHomePage());
    }

    public void backToHomePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void nextPlayerEntry(View view) {

        // change textview "Spooner 1" to "Spooner 2"
        name_txt_p1p2.setText("Spooner 2");
        name_txt_getName.setText("");

        // reset hint
        editText = findViewById(R.id.name_input);
        editText.setHint("eg. Mary");

        //reset color of editview
        name_txt_getName.setBackgroundColor(Color.rgb(255,255,255));

        name_btn_next.setOnClickListener((View v) -> {
            name_txt_getName = findViewById(R.id.name_input);
            String p2Name = name_txt_getName.getText().toString();
            if (p2Name.length() >0){
                Player player2 = new Player(p2Name, ContextCompat.getColor(this, R.color.p2_color));
                gameDetails.addPlayer(player2);
                Log.d("P2 name saved", p2Name);
                nextPage();
            } else{
                name_txt_getName.setBackgroundColor(Color.rgb(249,174,174));
            }
        });
    }

    public void nextPage(){
        Intent intent = new Intent(this, PToP.class);
        intent.putExtra("GameDetails", gameDetails);
        startActivity(intent);
    }
}