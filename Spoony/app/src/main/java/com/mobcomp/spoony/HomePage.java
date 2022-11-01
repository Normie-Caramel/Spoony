package com.mobcomp.spoony;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * This activity define the homepage when open up the app.
 * Including animations in the page and including two buttons that one lead to
 * trigger start playing and one lead to the question adding page.
 */
public class HomePage extends AppCompatActivity {

    ImageButton start_button;
    ImageButton firebase_button;
    GameDetails gd;

    AnimationDrawable catAnimation;
    ImageView shadow;
    ImageView phone;
    ImageView start_btn_bkg;
    TextView startTxt;
    TextView spoonyTxt;
    Animation startMsg_fadein;
    Animation startMsg_fadeout;
    ConstraintLayout egQuestion;
    Animation egQuestion_fadein;
    Animation egQuestion_fadeout;
    ConstraintLayout egRotate;
    Animation egRotate_fadein;
    Animation egRotate_fadeout;
    Animation moveLeftRight;
    Animation rotate;
    Animation blink;

    AudioService audioService;
    ServiceConnection connection = new ServiceConnection() {

        /**
         * define to start playing bgm once open up the app
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder) service;
            audioService = binder.getService();
            audioService.startHome();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    /**
     * define the start button and add question button that lead to other activities
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        start_button = (ImageButton)findViewById(R.id.btn_start);
        firebase_button = (ImageButton)findViewById(R.id.btn_add_question);

        start_button.setOnClickListener(this::jumpToStart);
        firebase_button.setOnClickListener(this::jumpToFirebase);

        gd = new GameDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AudioService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }

    /**
     * define all animations on homepage
     */
    @Override
    protected void onResume() {
        super.onResume();

        ImageView catView = (ImageView) findViewById(R.id.cat_anim);
        catView.setBackgroundResource(R.drawable.cat_animation);
        catAnimation = (AnimationDrawable) catView.getBackground();


        startMsg_fadein = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);
        egQuestion_fadein = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);
        egRotate_fadein = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);

        startMsg_fadeout = AnimationUtils.loadAnimation(this, R.anim.anim_fade_out);
        egQuestion_fadeout = AnimationUtils.loadAnimation(this, R.anim.anim_fade_out);
        egRotate_fadeout = AnimationUtils.loadAnimation(this, R.anim.anim_fade_out);

        rotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_phone);
        blink = AnimationUtils.loadAnimation(this,R.anim.anim_blink);

        egQuestion = (ConstraintLayout) findViewById(R.id.eg_question_layout);
        egRotate = (ConstraintLayout) findViewById(R.id.eg_rotate_layout);
        phone = (ImageView) findViewById(R.id.eg_phone_anim);
        startTxt = (TextView) findViewById(R.id.start_text);
        spoonyTxt = (TextView) findViewById(R.id.spoony_text);


        moveLeftRight = AnimationUtils.loadAnimation(this, R.anim.anim_move_leftright);
        moveLeftRight.setRepeatCount(Animation.INFINITE);

        shadow = (ImageView) findViewById(R.id.splash_black_circle);

        start_btn_bkg = (ImageView) findViewById(R.id.start_btn_bkg);



        // Animations on the three tutorial messages on the home screen
        startMsg_fadeout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                startMsg_fadein.setStartOffset(8000);
                startTxt.startAnimation(startMsg_fadein);
                spoonyTxt.startAnimation(startMsg_fadein);

            }
        });

        startMsg_fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                startMsg_fadeout.setStartOffset(2000);
                startTxt.startAnimation(startMsg_fadeout);
                spoonyTxt.startAnimation(startMsg_fadeout);
            }
        });

        egQuestion_fadein.setStartOffset(4000);
        egQuestion_fadeout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                egQuestion_fadein.setStartOffset(8000);
                egQuestion.startAnimation(egQuestion_fadein);

            }
        });

        egQuestion_fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                egQuestion_fadeout.setStartOffset(2000);
                egQuestion.startAnimation(egQuestion_fadeout);
            }
        });

        egRotate_fadein.setStartOffset(8000);
        egRotate_fadeout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                egRotate_fadein.setStartOffset(8050);
                egRotate.startAnimation(egRotate_fadein);

            }
        });


        egRotate_fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                egRotate_fadeout.setStartOffset(2000);
                egRotate.startAnimation(egRotate_fadeout);
            }
        });


        // calling all animations to start
        catAnimation.start();
        startTxt.startAnimation(startMsg_fadein);
        start_btn_bkg.startAnimation(blink);
        spoonyTxt.startAnimation(startMsg_fadein);
        egQuestion.startAnimation(egQuestion_fadein);
        egRotate.startAnimation(egRotate_fadein);
        phone.startAnimation(rotate);
        shadow.startAnimation(moveLeftRight);
    }

    /**
     * define pausing all animations once changing to activities other than home activity
     */
    @Override
    protected void onPause() {
        super.onPause();
        catAnimation.stop();
        shadow.clearAnimation();
        startTxt.clearAnimation();
        start_btn_bkg.clearAnimation();
        spoonyTxt.clearAnimation();
        egQuestion.clearAnimation();
        egRotate.clearAnimation();
        phone.clearAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }


    /**
     * define what start button does
     */
    public void jumpToStart(View view) {
        audioService.startGame();
        Intent intent = new Intent(this, Name.class);
        intent.putExtra("GameDetails", gd);
        startActivity(intent);
    }

    /**
     * define what add question button does
     */
    public void jumpToFirebase(View view) {

        Intent intent = new Intent(this, FirebaseActivity.class);
        startActivity(intent);
    }
}
