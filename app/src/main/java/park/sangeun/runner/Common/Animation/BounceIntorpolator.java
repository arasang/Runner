package park.sangeun.runner.Common.Animation;

import android.view.animation.Interpolator;

/**
 * Created by user on 2017-04-11.
 */

public class BounceIntorpolator implements Interpolator {
    private double mAmplitude = 1;
    private double mFrequency = 10;

    public BounceIntorpolator(double amplitude, double frequency){
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    @Override
    public float getInterpolation(float v) {
        return (float) (-1 * Math.pow(Math.E, - v/ mAmplitude) *
                Math.cos(mFrequency * v) + 1);
    }
}
