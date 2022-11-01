package com.mobcomp.spoony;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity define the page querying for player location
 */
public class PToP extends SpoonyActivity {

    private final int POINT_TO_PLR_LAYOUT = R.layout.ptp;
    private final int FLAT_PROMPT_LAYOUT = R.layout.down;
    private int currentLayout;

    private ImageButton confirmBtn;
    private ImageView innerCompass;
    private ImageView arrowP1;
    private TextView nameView;

    private boolean p1Located;

    private GameDetails gameDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // first prompt the user what to do next
        currentLayout = FLAT_PROMPT_LAYOUT;
        setContentView(currentLayout);
        commonBtnSetup();

        p1Located = false;
        gameDetails = getGameDetails();
    }

    @Override
    public void onBackPressed() {
        // user may switch within this activity to reset locations
        if(p1Located) {
            p1Located = false;
            nameSetup();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onEnterTable() {
        // when user put the phone on tha table, show compass and
        // arrows to assist them with location setting
        currentLayout = POINT_TO_PLR_LAYOUT;
        setContentView(currentLayout);
        nameSetup();
        btnSetup();
        compassSetup();
    }

    @Override
    protected void updateTable() {
        // after the first player finishing location setting, show
        // relative position to the second player
        if(p1Located && currentLayout == POINT_TO_PLR_LAYOUT) {
            float offset = Angle.rotationDistanceSigned(
                    deviceOrientation[0],
                    gameDetails.getLead().getDirection());
            innerCompass.setRotation(offset);
            arrowP1.setRotation(offset);
        }
    }

    @Override
    protected void onExitTable() {
        // prompt user to place the phone on the table
        currentLayout = FLAT_PROMPT_LAYOUT;
        setContentView(currentLayout);
        commonBtnSetup();
    }

    /**
     * set the prompt name according to the player in turn
     */
    private void nameSetup() {
        if (currentLayout == POINT_TO_PLR_LAYOUT) {
            nameView = (TextView) findViewById(R.id.entry_name_p1);
            Player player = p1Located ? gameDetails.getFollow() : gameDetails.getLead();
            nameView.setText(player.getName());
            nameView.setTextColor(player.getColour());
        }
    }

    /**
     * bind the button exclusive to the point-to-player layout
     */
    private void btnSetup() {
        if (currentLayout == POINT_TO_PLR_LAYOUT) {
            confirmBtn = (ImageButton) findViewById(R.id.lock_button);
            confirmBtn.setOnClickListener((View v) -> onConfirmPressed());
            commonBtnSetup();
        }
    }

    /**
     * bind the compass and arrow to layout entities
     */
    private void compassSetup() {
        if (currentLayout == POINT_TO_PLR_LAYOUT) {
            innerCompass = (ImageView) findViewById(R.id.imageView_compass_inner_circle);
            arrowP1 = (ImageView) findViewById(R.id.imageView_compass_arrow_p1);
        }
    }

    /**
     * define the response when user press the confirm button, handle the illegal
     * location setting and activity switches
     */
    private void onConfirmPressed() {
        float zOrientation = deviceOrientation[0];
        if (!p1Located) {
            // initial state, player 1 set location and prompt player 2
            gameDetails.getLead().setDirection(zOrientation);
            p1Located = true;
            nameSetup();
        } else {
            // in order to make gameplay part reasonable, player 2 should sit opposite to
            // the player 1 with some tolerance
            float offset = Math.abs(zOrientation - gameDetails.getLead().getDirection());
            if (offset > 135 && offset < 215) {
                gameDetails.getFollow().setDirection(zOrientation);
                changeActivity(QuestionActivity.class, gameDetails);
            } else {
                Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_refuse_shake);
                confirmBtn.startAnimation(shake);
                String prompt = "Please sit opposite to " + gameDetails.getLead().getName();
                Toast toast = Toast.makeText(this, prompt, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}