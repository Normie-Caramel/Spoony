package com.mobcomp.spoony;

import org.junit.Test;

import static org.junit.Assert.*;

public class AngleTest {

    @Test
    public void backwardsRotationUnsigned() { assertEquals(150f, Angle.rotationDistanceUnsigned(60f, -90f), 0.1f); }

    @Test
    public void forwardsRotationUnsigned() { assertEquals(150f, Angle.rotationDistanceUnsigned(-90f, 60f), 0.1f); }

    @Test
    public void noRotationUnsigned() { assertEquals(0f, Angle.rotationDistanceUnsigned(45f, 45f), 0.1f); }

    @Test
    public void rotationAcross180Unsigned() { assertEquals(100f, Angle.rotationDistanceUnsigned(-90f, 170f), 0.1f); }

    @Test
    public void rotationAbove360Unsigned() { assertEquals(100f, Angle.rotationDistanceUnsigned(Angle.normaliseAngle(730f), -90f), 0.1f); }




    @Test
    public void backwardsRotationSigned() { assertEquals(-150f, Angle.rotationDistanceSigned(60f, -90f), 0.1f); }

    @Test
    public void forwardsRotationSigned() { assertEquals(150f, Angle.rotationDistanceSigned(-90f, 60f), 0.1f); }

    @Test
    public void noRotationSigned() { assertEquals(0f, Angle.rotationDistanceSigned(45f, 45f), 0.1f); }

    @Test
    public void rotationAcross180Signed() { assertEquals(-100f, Angle.rotationDistanceSigned(-90f, 170f), 0.1f); }

    @Test
    public void rotationAbove360Signed() { assertEquals(-100f, Angle.rotationDistanceSigned(Angle.normaliseAngle(730f), -90f), 0.1f); }



    @Test
    public void percentBetweenSmall() { assertEquals(0.75f, Angle.percentageBetween(30f, 0f, 40f), 0.1f); }

    @Test
    public void percentBetweenLarge() { assertEquals(0.50f, Angle.percentageBetween(90f, 0f, 180f), 0.1f); }

    @Test
    public void percentBetweenCrossing() { assertEquals(0.25f, Angle.percentageBetween(-170f, 170f, -110f), 0.1f);}





    @Test
    public void normaliseRotationLarge() { assertEquals(45f, Angle.normaliseAngle(765f), 0.1f); }

    @Test
    public void normaliseRotationMedium() { assertEquals(-165f, Angle.normaliseAngle(195f), 0.1f); }

    @Test
    public void normaliseRotationSmall() { assertEquals(70f, Angle.normaliseAngle(70f), 0.1f); }

    @Test
    public void normaliseRotationNegative() { assertEquals(-60f, Angle.normaliseAngle(-420f), 0.1f); }
}
