package com.mobcomp.spoony;

import java.util.ArrayList;
import java.util.Collections;

// the filter introduces a delay of (bufferSize - 1) / 2 frames since the gaussian curve is centred
// on the midpoint of the buffer
public class GaussianMedianDeltaFilter {

    private final float width; // weierstrass width (t), determines intensity of smoothing
    private final int bufferSize;
    private ArrayList<Float> buffer;
    private ArrayList<Float> deltas;
    private ArrayList<Float> sortedDeltas;

    public GaussianMedianDeltaFilter(int bufferSize, float weierstrassWidth) {
        this.bufferSize = bufferSize;
        this.width = weierstrassWidth;
        buffer = new ArrayList<>();
        deltas = new ArrayList<>();
    }

    /**
     * Adds a datapoint to the filter buffer, freeing space for it if the buffer is full.
     * @param x the value to add
     */
    public void add(float x) {
        if (buffer.size() >= bufferSize) {
            buffer.remove(0);
        }

        buffer.add(x);
    }

    /**
     * Reads the current smoothed value from the filter. The filter uses the differences of the values
     * from the median value so that it can seamlessly cross the discontinuous portion of the angle
     * data stream (when the device orientation crosses from -180 to 180). The filter calculates
     * the smoothed value using a gaussian curve centred at the median delta value.
     * @return the smoothed value
     */
    public float read() {
        // use midpoint as centre of gaussian distribution
        int midpoint = (buffer.size() - 1) / 2;
        float baseValue = buffer.get(midpoint);


        deltas.clear();
        // calculate angle deltas (which are rotation 'agnostic')
        for (Float n : buffer) {
            deltas.add(Angle.rotationDistanceUnsigned(baseValue, n));
        }

        // sort deltas so that the median value is the centre of our gaussian distribution
        sortedDeltas = (ArrayList<Float>) deltas.clone();
        Collections.sort(sortedDeltas);

        float resultDelta = 0f;

        for (int i = 0; i < deltas.size(); i++) {
            int x = i - midpoint; // centre the frame around origin 0

            // calculate contribution of each value based on gaussian curve
            resultDelta += deltas.get(i) *
                    ((1f / (Math.sqrt(4f * Math.PI * width))) *
                    Math.pow(Math.E, -1f * ((x * x) / (4f * width))));
        }

        return Angle.normaliseAngle(baseValue + resultDelta);
    }
}
