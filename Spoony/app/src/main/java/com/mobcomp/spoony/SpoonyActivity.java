package com.mobcomp.spoony;

import static com.mobcomp.spoony.Angle.rotationDistanceUnsigned;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;

import android.os.Bundle;

/**
 * This class is the framework of game logic, which follows the state machine model. The methods,
 * which handle the interactions with sensors, are also defined here. The activities need to change
 * prompt or response according to phone position (on the table or hand) will extend this class.
 */
public class SpoonyActivity extends GameActivity implements SensorEventListener {

    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_UI; // 60ms, the interval between sensor reports
    private static final float TABLE_THRESHOLD = -20.f; // y-bearing above which the phone is considered 'on the table'
    private static final float VIEW_DISTANCE = 80.0f; // degrees from player position that counts as being in their 'view'
    private static final int TRANSITION_FRAMES = 3; // the number of frames a new state must maintain before we change to it
    private static final int GAUSSIAN_FILTER_SIZE = 7;
    private static final float GAUSSIAN_FILTER_WIDTH = 5f;

    // fake state machine
    private SpoonyState state = SpoonyState.DEFAULT;
    private SpoonyState prevState = SpoonyState.DEFAULT; // state must have changed for several frames to register (to avoid jitter)
    private int framesInState = 0;

    // orientation sensors
    private SensorManager sensorManager;
    private final float[] accelReading = new float[3];
    private final float[] magReading = new float[3];

    // orientation calculation
    private final float[] rotationMatrix = new float[9];
    private final float[] deviceOrientationRadians = new float[3];
    public float[] deviceOrientation = new float[3];
    private GaussianMedianDeltaFilter xRotationFilter;

    // player positions
    private float leadPosition = 0.0f;
    private float followPosition = 180.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        xRotationFilter = new GaussianMedianDeltaFilter(GAUSSIAN_FILTER_SIZE, GAUSSIAN_FILTER_WIDTH);

        // fetch game details
        GameDetails gameDetails = getGameDetails();
        if (gameDetails == null) gameDetails = new GameDetails();

        if (gameDetails.getLead() != null) leadPosition = gameDetails.getLead().getDirection();
        if (gameDetails.getFollow() != null) followPosition = gameDetails.getFollow().getDirection();
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

    // this gets called when EITHER sensor changes
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

            // filter only x-rotation to prevent flicker - it is the only rotation displayed in the UI
            xRotationFilter.add((float) Math.toDegrees(deviceOrientationRadians[0]));

            deviceOrientation[0] = xRotationFilter.read();
            deviceOrientation[1] = (float) Math.toDegrees(deviceOrientationRadians[1]);
            deviceOrientation[2] = (float) Math.toDegrees(deviceOrientationRadians[2]);

            checkState(deviceOrientation);
            update();
        }
    }

    /**
     * Determines the current state of the device based on its orientation
     * @param orientation the device orientation to derive the state from
     */
    private void checkState(float[] orientation) {

        // phone is within table surface rotation limit
        if (orientation[1] > TABLE_THRESHOLD) {
            setState(SpoonyState.TABLE);
        }

        // phone is within VIEW_DISTANCE degrees of player 1's position
        else if (rotationDistanceUnsigned(orientation[0], leadPosition) < VIEW_DISTANCE) {
            // note that if the z-rotation leaves the bounds [-90, 90] then the x-rotation is flipped
            if (orientation[2] > -90 && orientation[2] < 90) setState(SpoonyState.FOLLOW_VIEW);
            else setState(SpoonyState.LEAD_VIEW);

            // when the phone is pointing at the lead player, it is instead the follow player who
            // sees the screen, therefore the cases are reversed
        }

        // phone is within VIEW_DISTANCE degrees of player 2's position
        else if (rotationDistanceUnsigned(orientation[0], followPosition) < VIEW_DISTANCE) {
            // again, handle flipping when leaving z-rot [-90, 90]
            if (orientation[2] > -90 && orientation[2] < 90) setState(SpoonyState.LEAD_VIEW);
            else setState(SpoonyState.FOLLOW_VIEW);
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

    /**
     * Changes state, triggering entry methods
     * @param state the state to enter
     */
    private void enterState(SpoonyState state) {
        switch (state) {
            case TABLE:
                onEnterTable();
                break;
            case LEAD_VIEW:
                onEnterLeadView();
                break;
            case FOLLOW_VIEW:
                onEnterFollowView();
                break;
            default:
                onEnterDefault();
        }
    }

    /**
     * Exits state, triggering exit methods
     * @param state the state to exit
     */
    private void exitState(SpoonyState state) {
        switch (state) {
            case TABLE:
                onExitTable();
                break;
            case LEAD_VIEW:
                onExitLeadView();
                break;
            case FOLLOW_VIEW:
                onExitFollowView();
                break;
            default:
                onExitDefault();
        }
    }

    /**
     * Called every time the magnetometer reading is updated, providing an update loop to child activities
     */
    private void update() {

        updateAlways();

        switch (state) {
            case TABLE:
                updateTable();
                break;
            case LEAD_VIEW:
                updateLeadView();
                break;
            case FOLLOW_VIEW:
                updateFollowView();
                break;
            default:
                updateDefault();
        }
    }

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {} // do nothing
}