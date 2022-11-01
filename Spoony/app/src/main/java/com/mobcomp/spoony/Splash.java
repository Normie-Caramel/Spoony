package com.mobcomp.spoony;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity define the welcome page (with animation) when entering
 * the app from the outside
 */
public class Splash extends AppCompatActivity {

    ImageView shadow;
    ImageView blur_circular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // configure our window to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);

        shadow = findViewById(R.id.splash_black_circle);
        blur_circular = findViewById(R.id.shadow_circular);


        Animation anim_blink_splash = AnimationUtils.loadAnimation(this,R.anim.anim_blink_splash);
        Animation anim_zoom_out = AnimationUtils.loadAnimation(this,R.anim.anim_zoom_out);
        Animation anim_zoom_in = AnimationUtils.loadAnimation(this,R.anim.anim_zoom_in);

        anim_zoom_out.setStartOffset(100);
        anim_zoom_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                shadow.startAnimation(anim_zoom_in);
            }
        });

        shadow.startAnimation(anim_zoom_out);
        blur_circular.startAnimation(anim_blink_splash);


        // call handler to run a task for specific time interval
        new Handler().postDelayed(() -> {
            // creating a new intent
            Intent i = new Intent(Splash.this, HomePage.class);
            startActivity(i);

            // finish our current activity.
            finish();
        }, 2500);

    }
}
