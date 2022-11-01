package com.mobcomp.spoony;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity handles the common logic used by all activities related to
 * the gameplay (i.e. except home and firebase), and thus be the parent class
 * of those activities.
 */
public class GameActivity extends AppCompatActivity {

    ImageButton homeBtn;
    ImageButton backBtn;
    AudioService audioService;

    // subscribe to the audio service
    ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            audioService = binder.getService();
            // handle the case user enter the game directly from outside
            if (!audioService.getIsPlaying()) {
                audioService.startGame();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AudioService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    /**
     * define the home button at the top right corner. Once user press it, he/her
     * will be asked for confirmation to exit the game.
     */
    protected void showHomeDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage("Are you sure you want to exit the game?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("YES", (dialogInterface, i) -> goBackHome());
        dialog.setNegativeButton("NOT REALLY", (dialogInterface, i) -> {});
        dialog.show();
    }

    /**
     * go to the homepage and clear the activities stack
     */
    protected void goBackHome() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * bind the common used button i.e. back button and home button
     */
    protected void commonBtnSetup() {
        homeBtn = (ImageButton) findViewById(R.id.home_btn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener((View v) -> showHomeDialog());
        }

        backBtn = (ImageButton) findViewById(R.id.back_btn);
        if (backBtn != null) {
            backBtn.setOnClickListener((View v) -> onBackPressed());
        }
    }

    /**
     * get the GameDetails, which is a message carrier passed through activities.
     * @return the GameDetails instance passed in by previous activity
     */
    public GameDetails getGameDetails() {
        Intent intent = getIntent();
        return (GameDetails) intent.getSerializableExtra("GameDetails");
    }

    /**
     * switch from current activity to next one, with the game details carried on.
     * @param cls the target activity that we want to switch to
     * @param gameDetails a message carrier storing the game state
     */
    public void changeActivity(Class<?> cls, GameDetails gameDetails) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("GameDetails", gameDetails);
        startActivity(intent);
    }
}