package com.mobcomp.spoony;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Map;

/**
 * This activity defines the page where users uplaod their own questions to the database
 */
@SuppressWarnings("ALL")
public class FirebaseActivity extends AppCompatActivity {

    private Map<String, Object> qs;

    private Button uButton;
    private EditText qTextInput;
    private TextView outputTextView;
    private String questionJSON;

    private ConstraintLayout _layout;
    ImageButton backBtn;
    ImageView homeBtn;

    FirebaseHandler fb = new FirebaseHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib);

        backBtnSetup();
        _layout = findViewById(R.id.firebaseLayout);
        uButton = findViewById(R.id.uploadButton);
        qTextInput = findViewById(R.id.questionTextInput);
        outputTextView = findViewById(R.id.outputTextView);

        uButton.setOnClickListener(view -> {
            String input = qTextInput.getText().toString();
            Log.d("QIN", input);
            outputTextView.setText("Success !");
            outputTextView.setTextColor(Color.WHITE);

            // disabled for now to prevent accidentally adding stuff to firestore
            fb.addNewQuestion(input, success -> {
                if (success) {
                    Toast.makeText(this, "Question added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Question error!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void backBtnSetup() {
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        backBtn.setOnClickListener((View v) -> onBackPressed());
    }

}