package com.mobcomp.spoony;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Name extends GameActivity {

    Button name_btn_next;
    TextView name_txt_getName;
    TextView name_txt_p1p2;
    GameDetails gameDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name);

        if (gameDetails == null) {
            Intent intent = getIntent();
            gameDetails = (GameDetails) intent.getSerializableExtra("GameDetails");
        }

        name_txt_p1p2 = findViewById(R.id.textView_p1p2);
        name_txt_p1p2.setText("Spooner 1");
        // save player names to GameDetails
        name_btn_next = (Button) findViewById(R.id.next_button);
        name_btn_next.setOnClickListener((View v) -> {
            name_txt_getName = findViewById(R.id.name_input_p1);
            String p1Name = name_txt_getName.getText().toString();

            Player player1 = new Player(p1Name, ContextCompat.getColor(this, R.color.p1_color));
            gameDetails.addPlayer(player1);
            Log.d("P1 name saved", p1Name);
            nextPlayerEntry(v);
        });
    }

    public void nextPlayerEntry(View view) {

        // change textview "Spooner 1" to "Spooner 2"
        name_txt_p1p2.setText("Spooner 2");
        name_txt_getName.setText("");
        name_btn_next.setOnClickListener((View v) -> {
            name_txt_getName = findViewById(R.id.name_input_p1);
            String p2Name = name_txt_getName.getText().toString();

            Player player2 = new Player(p2Name, ContextCompat.getColor(this, R.color.p2_color));
            gameDetails.addPlayer(player2);

            Log.d("P2 name saved", p2Name);
            nextPage();
        });
    }

    public void nextPage(){
        Intent intent = new Intent(this, PToP.class);
        intent.putExtra("GameDetails", gameDetails);
        startActivity(intent);
    }
}