package com.mobcomp.spoony;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Map;

@SuppressWarnings("ALL")
public class FirebaseActivity extends AppCompatActivity {

    private Map<String, Object> qs;

    private Button uButton;
    private EditText qTextInput;
    private TextView outputTextView;
    private String questionJSON;

    private CoordinatorLayout _layout;

    FirebaseHandler fb = new FirebaseHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib);

        _layout = findViewById(R.id.firebaseLayout);
        uButton = findViewById(R.id.uploadButton);
        qTextInput = findViewById(R.id.questionTextInput);
        outputTextView = findViewById(R.id.outputTextView);

        uButton.setOnClickListener(view -> {
            String input = qTextInput.getText().toString();
            Log.d("QIN", input);
            outputTextView.setText("SUCCESS");
            outputTextView.setBackgroundColor(Color.BLACK);
            outputTextView.setTextColor(Color.GREEN);

            // disabled for now to prevent accidentally adding stuff to firestore
            fb.addNewQuestion(input, success -> {
                if (success) {
                    outputTextView.setText("SUCCESS");
                    outputTextView.setBackgroundColor(Color.BLACK);
                } else {
                    outputTextView.setText("ERROR");
                    outputTextView.setTextColor(Color.RED);
                    outputTextView.setBackgroundColor(Color.BLACK);
                }
            });
        });
    }
}