package com.mobcomp.spoony;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;

import android.os.Bundle;

public class SpoonyActivity extends AppCompatActivity implements SensorEventListener {

    private static GameDetails gameDetails;

    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_UI; // 60ms, the interval between sensor reports
    private static final float TABLE_THRESHOLD = -20.f; // y-bearing above which the phone is considered 'on the table'
    private static final float VIEW_DISTANCE = 80.0f; // degrees from player position that counts as being in their 'view'
    private static final float TRANSITION_FRAMES = 3; // the number of frames a new state must maintain before we change to it

    // fake state machine
    private SpoonyState state = SpoonyState.DEFAULT;
    private SpoonyState prevState = SpoonyState.DEFAULT; // state must have changed for two frames to register (to avoid jitter)
    private int framesInState = 0;

    // orientation sensors
    private SensorManager sensorManager;
    private final float[] accelReading = new float[3];
    private final float[] magReading = new float[3];

    // orientation calculation
    private final float[] rotationMatrix = new float[9];
    private final float[] deviceOrientationRadians = new float[3];
    public float[] deviceOrientation = new float[3];

    // player positions
    private SharedPreferences data;
    private float leadPosition = 0.0f;
    private float followPosition = 180.0f;

    private GameDetails gameDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // fetch game details
        Intent intent = getIntent();
        gameDetails = (GameDetails) intent.getSerializableExtra("GameDetails");

        gameDetails = new GameDetails();

        _p1Position = _data.getFloat(Key.P1_POSITION, 0.0f);
        _p2Position = _data.getFloat(Key.P2_POSITION, 0.0f);
        data = getSharedPreferences(Key.DEFAULT_PREFERENCES, MODE_PRIVATE);

        //leadPosition = data.getFloat(Key.P1_POSITION, 0.0f);
        //followPosition = data.getFloat(Key.P2_POSITION, 0.0f);
        if (gameDetails != null) {
            leadPosition = gameDetails.getLead().getDirection();
            followPosition = gameDetails.getFollow().getDirection();
        }
    }

    // this gets called on creation (later than onCreate), or when the user returns to the app after minimising it
    @Override
    protected void onResume() {
        super.onResume();

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SENSOR_DELAY, SENSOR_DELAY);
        }

        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SENSOR_DELAY, SENSOR_DELAY);
        }
    }

    // this is called on app close or on minimise, to turn off sensing when not required
    @Override
    protected void onPause() {
        super.onPause();

        // this unsubscribes from ALL sensors
        sensorManager.unregisterListener(this);
    }

    // this gets called when EITHER sensor changes - do we want to update the values every time?
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // check which sensor has been updated
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // accelerometer update
            System.arraycopy(sensorEvent.values, 0 , accelReading, 0, accelReading.length);

        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // magnetometer update
            System.arraycopy(sensorEvent.values, 0 , magReading, 0, magReading.length);

            // only update on mag change
            SensorManager.getRotationMatrix(rotationMatrix, null, accelReading, magReading); // uses gravity and geo readings to calculate orientation vectors
            SensorManager.getOrientation(rotationMatrix, deviceOrientationRadians); // convert orientation vectors into radians
            deviceOrientation[0] = (float) Math.toDegrees(deviceOrientationRadians[0]);
            deviceOrientation[1] = (float) Math.toDegrees(deviceOrientationRadians[1]);
            deviceOrientation[2] = (float) Math.toDegrees(deviceOrientationRadians[2]);

            // TODO: just use radians in state calculation?

            checkState(deviceOrientation);
            update();
        }
    }

    private void checkState(float[] orientation) {

        // phone is within table surface rotation limit
        if (orientation[1] > TABLE_THRESHOLD) {
            setState(SpoonyState.TABLE);
        }

        // phone is within VIEW_DISTANCE degrees of player 1's position
        else if (rotationDistanceUnsigned(orientation[0], leadPosition) < VIEW_DISTANCE) {
            // note that if the z-rotation leaves the bounds [-90, 90] then the x-rotation is flipped
            if (orientation[2] > -90 && orientation[2] < 90) setState(SpoonyState.P1_VIEW);
            else setState(SpoonyState.P2_VIEW);
        }

        // phone is within VIEW_DISTANCE degrees of player 2's position
        else if (rotationDistanceUnsigned(orientation[0], followPosition) < VIEW_DISTANCE) {
            // again, handle flipping when leaving z-rot [-90, 90]
            if (orientation[2] > -90 && orientation[2] < 90) setState(SpoonyState.P2_VIEW);
            else setState(SpoonyState.P1_VIEW);
        }

        // none of the above, fall back to default
        else {
            setState(SpoonyState.DEFAULT);
        }
    }

    /**
     * Checks to see if the new state has lasted at least TRANSITION_FRAMES frames - and if it has,
     * changes to the new state, calling the appropriate transition functions as it does so.
     * @param newState the state to transition to
     */
    private void setState(SpoonyState newState) {
        if (newState == state) return;

        // check that the new state has persisted for enough frames that we can confidently change to it
        // this should reduce jitter (i.e. cases where an incorrect state is detected for a single frame)
        if (framesInState <= TRANSITION_FRAMES) {
            if (newState == prevState) framesInState++;
            else {
                prevState = newState;
            }
            return;
        }

        // if it has, we can continue as normal
        framesInState = 0;
        exitState(state);
        state = newState;
        enterState(state);
    }

    private void enterState(SpoonyState state) {
        switch (this.state) {
            case TABLE:
                onEnterTable();
                break;
            case P1_VIEW:
                onEnterLeadView();
                break;
            case P2_VIEW:
                onEnterFollowView();
                break;
            default:
                onEnterDefault();
        }
    }

    private void exitState(SpoonyState state) {
        switch (state) {
            case TABLE:
                onExitTable();
                break;
            case P1_VIEW:
                onExitLeadView();
                break;
            case P2_VIEW:
                onExitFollowView();
                break;
            default:
                onExitDefault();
        }
    }

    private void update() {

        updateAlways();

        switch (state) {
            case TABLE:
                updateTable();
                break;
            case P1_VIEW:
                updateLeadView();
                break;
            case P2_VIEW:
                updateFollowView();
                break;
            default:
                updateDefault();
        }
    }

    public static GameDetails getGameDetails() {
        return gameDetails;
    }

    // ANGLE HELPERS

    // calculates (unsigned) distance between two angles
    public static float rotationDistanceUnsigned(float a, float b) {
        return 180 - Math.abs((Math.abs(a - b) % 360) - 180);
    }

    // calculates signed distance between two angles TODO: this is borked
    public static float rotationDistanceSigned(float a, float b) {
        float d = rotationDistanceUnsigned(a, b);
        int sign = 1;
        if (d >= 180 || normaliseAngle(a) > normaliseAngle(b)) sign = -1;

        return sign * d;
    }

    // returns angle within [0, 360)
    public static float normaliseAngle(float a) {
        return ((360 + (a % 360)) % 360);
    }

    // calculates angle relative to the screen given a real-world orientation angle TODO: also borked
    public float worldToScreenRotation(float worldPosition) {
        return rotationDistanceSigned(deviceOrientation[0], worldPosition);
    }


    // STATE CALLS

    public SpoonyState getState() {
        return state;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {} // do nothing


    // these methods are provided to child activities to easily change displayed information based on the device state
    protected void onEnterLeadView() {}
    protected void updateLeadView() {}
    protected void onExitLeadView() {}

    protected void onEnterFollowView() {}
    protected void updateFollowView() {}
    protected void onExitFollowView() {}

    protected void onEnterTable() {}
    protected void updateTable() {}
    protected void onExitTable() {}

    protected void onEnterDefault() {}
    protected void updateDefault() {}
    protected void onExitDefault() {}

    protected void updateAlways() {}
}