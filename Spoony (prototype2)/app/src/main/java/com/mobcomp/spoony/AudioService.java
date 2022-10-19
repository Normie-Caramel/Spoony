package com.mobcomp.spoony;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class AudioService extends Service {

    private final IBinder audioBinder = new AudioBinder();
    private MediaPlayer gameBGM;
    private MediaPlayer homeBGM;
    private boolean isPlaying = false;

    public class AudioBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        init();
        return audioBinder;
    }

    @Override
    public void onDestroy() {
        if (gameBGM != null) {
            gameBGM.stop();
            gameBGM.release();
        }
        if (homeBGM != null) {
            homeBGM.stop();
            homeBGM.release();
        }
        super.onDestroy();
    }

    public void init() {
        gameBGM = MediaPlayer.create(getApplicationContext(), R.raw.home_bgm);
        homeBGM = MediaPlayer.create(getApplicationContext(), R.raw.home_bgm);
    }

    public void startGame() {
        if (homeBGM.isPlaying()) {
            homeBGM.pause();
        }
        gameBGM.seekTo(0);
        gameBGM.start();
        gameBGM.setLooping(true);
        isPlaying = true;
    }

    public void startHome() {
        if (gameBGM.isPlaying()) {
            gameBGM.pause();
        }
        homeBGM.seekTo(0);
        homeBGM.start();
        homeBGM.setLooping(true);
        isPlaying = true;
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }
}