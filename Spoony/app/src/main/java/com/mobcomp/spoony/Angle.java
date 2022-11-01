package com.mobcomp.spoony;

public class Angle {

    /**
     *
     * @param from the initial angle
     * @param to the target angle (although, when unsigned, these two angles are interchangeable)
     * @return the shortest distance between the two angles
     */
    public static float rotationDistanceUnsigned(float from, float to) {
        return 180 - Math.abs((Math.abs(to - from) % 360) - 180);
    }

    /**
     * Given an angle that lies upon the path between two other angles, this method determines
     * how far along that path the target angle sits
     * @param target the angle ON the path
     * @param from the angle at the beginning of the path
     * @param to the angle at the end of the path
     * @return the percentage [0.0, 1.0] of the target along the path
     */
    public static float percentageBetween(float target, float from, float to) {
        return rotationDistanceUnsigned(target, from) /
                (rotationDistanceUnsigned(target, from) + rotationDistanceUnsigned(target, to));
    }


    /**
     * Requires normalised angles.
     * @param from the initial angle (normalised)
     * @param to the target angle (normalised)
     * @return the rotation required to move from the initial angle to the target angle, where
     * negative is anti-clockwise and positive is clockwise
     */
    public static float rotationDistanceSigned(float from, float to) {
        float rotation = to - from;

        if (rotation > 180) return rotation - 360f;
        if (rotation < -180) return rotation + 360f;
        else return rotation;
    }

    /**
     * Normalises an angle to the range (-180, 180]
     * @param a the angle to normalise
     * @return the equivalent angle within (-180, 180]
     */
    public static float normaliseAngle(float a) {
        return (360 + ((a + 180) % 360)) % 360 - 180f;
    }
}
