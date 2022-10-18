package com.mobcomp.spoony;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class PToP extends SpoonyActivity {

    private final int POINT_TO_PLR_LAYOUT = R.layout.ptp;
    private final int FLAT_PROMPT_LAYOUT = R.layout.down;
    private int currentLayout;

    private ImageButton confirmBtn;
    private ImageButton backBtn;
    private ImageButton homeBtn;
    private ImageView innerCompass;
    private TextView nameView;

    private boolean p1Located;

    private GameDetails gameDetails;

//    private AudioService audioService;
//    private ServiceConnection connection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
//            audioService = binder.getService();
//            audioService.startGame();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentLayout = FLAT_PROMPT_LAYOUT;
        setContentView(currentLayout);
        commonBtnSetup();

        p1Located = false;
        gameDetails = getGameDetails();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Intent intent = new Intent(this, AudioService.class);
//        bindService(intent, connection, Context.BIND_AUTO_CREATE);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        unbindService(connection);
//    }

    @Override
    public void onBackPressed() {
        if(p1Located) {
            p1Located = false;
            nameSetup();
        } else {
            Intent intent = new Intent(this, Name.class);
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onEnterTable() {
        currentLayout = POINT_TO_PLR_LAYOUT;
        setContentView(currentLayout);
        nameSetup();
        exclusiveBtnSetup();
        compassSetup();
    }

    @Override
    protected void updateTable() {
        if(p1Located && currentLayout == POINT_TO_PLR_LAYOUT) {
            innerCompass.setRotation(Angle.rotationDistanceSigned(deviceOrientation[0], gameDetails.getLead().getDirection()));
        }
    }

    @Override
    protected void onExitTable() {
        currentLayout = FLAT_PROMPT_LAYOUT;
        setContentView(currentLayout);
        commonBtnSetup();
    }

    private void nameSetup() {
        if (currentLayout == POINT_TO_PLR_LAYOUT) {
            nameView = (TextView) findViewById(R.id.entry_name_p1);
            if (!p1Located) {
                nameView.setText(gameDetails.getLead().getName());
                nameView.setTextColor(gameDetails.getLead().getColour());
            } else {
                nameView.setText(gameDetails.getFollow().getName());
                nameView.setTextColor(gameDetails.getFollow().getColour());
            }
        }
    }

    private void exclusiveBtnSetup() {
        confirmBtn = (ImageButton) findViewById(R.id.lock_button);
        confirmBtn.setOnClickListener((View v) -> onConfirmPressed());
        commonBtnSetup();
    }

    private void commonBtnSetup() {
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        backBtn.setOnClickListener((View v) -> onBackPressed());
        homeBtn = (ImageButton) findViewById(R.id.home_btn);
        homeBtn.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, HomePage.class);
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void compassSetup() {
        innerCompass = (ImageView) findViewById(R.id.imageView_compass_inner_circle);
    }

    private void onConfirmPressed() {
        float zOrientation = deviceOrientation[0];
        Log.d("Orientation Prompt", String.valueOf(zOrientation));
        if (!p1Located) {
            gameDetails.getLead().setDirection(zOrientation);
            p1Located = true;
            nameSetup();
        } else {
            gameDetails.getFollow().setDirection(zOrientation);
            changeActivity(QuestionActivity.class, gameDetails);
        }
    }
}
