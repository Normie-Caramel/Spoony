package com.mobcomp.spoony;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

/**
 * This class handle the audio player, which play the BGM continuously. The BGM will
 * change once player enter the game from home activity and vice versa.
 */
public class AudioService extends Service {

    private final IBinder audioBinder = new AudioBinder();
    private MediaPlayer gameBGM;
    private MediaPlayer homeBGM;
    private boolean isPlaying = false;

    public class AudioBinder extends Binder {
        // return this service itself to enable master classes to call its member functions
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

    /**
     * load and decode (prepare) the audio assets from the file, this process
     * is handled in activity thread since the assets are small and won't cause
     * perceptible delay. Otherwise it should be handled in a separate thread.
     */
    public void init() {
        gameBGM = MediaPlayer.create(getApplicationContext(), R.raw.game_bgm);
        homeBGM = MediaPlayer.create(getApplicationContext(), R.raw.home_bgm);
    }

    /**
     * Stop home music and play the game music from the start
     */
    public void startGame() {
        if (homeBGM.isPlaying()) {
            homeBGM.pause();
        }
        gameBGM.seekTo(0);
        gameBGM.start();
        gameBGM.setLooping(true);
        isPlaying = true;
    }

    /**
     * Stop game music and play the home music from the start
     */
    public void startHome() {
        if (gameBGM.isPlaying()) {
            gameBGM.pause();
        }
        homeBGM.seekTo(0);
        homeBGM.start();
        homeBGM.setLooping(true);
        isPlaying = true;
    }

    /**
     * get whether there is any music is playing
     * @return true if any music is playing otherwise false
     */
    public boolean getIsPlaying() {
        return isPlaying;
    }
}