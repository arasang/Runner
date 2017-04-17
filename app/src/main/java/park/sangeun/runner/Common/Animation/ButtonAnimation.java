package park.sangeun.runner.Common.Animation;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-12.
 */

public class ButtonAnimation {

    private Context context;

    public ButtonAnimation(Context context){
        this.context = context;
    }

    public void onButtonOn(View v, int colorId){
        int colorStart = context.getResources().getColor(android.R.color.darker_gray);
        int colorEnd   = context.getResources().getColor(colorId);
        ValueAnimator colorAnim = ObjectAnimator.ofInt(v, "backgroundColor", colorStart, colorEnd);

        colorAnim.setDuration(500);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();
    }

    public void onButtonOff(View v, int colorId){
        int colorEnd   = context.getResources().getColor(android.R.color.darker_gray);
        int colorStart = context.getResources().getColor(colorId);
        ValueAnimator colorAnim = ObjectAnimator.ofInt(v, "backgroundColor", colorStart, colorEnd);

        colorAnim.setDuration(500);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();
    }

    public void onButtonBounce(View button){
        Animation bounce = AnimationUtils.loadAnimation(context, R.anim.filled_circle);
        BounceIntorpolator interpolator = new BounceIntorpolator(0.2, 20);
        bounce.setInterpolator(interpolator);
        button.startAnimation(bounce);
    }

}
