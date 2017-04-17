package park.sangeun.runner.Common;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by user on 2017-04-04.
 */

public class ConvertPixel {
    private static Context context;

    public ConvertPixel(Context mContext){
        context = mContext;
    }

    public int PxToDp(int Px){
        return (int) (Px / context.getResources().getDisplayMetrics().density);
    }

    public int DpToPx(int Dp){
        return (int) (Dp * context.getResources().getDisplayMetrics().density);
    }

    public int[] GetDisplaySize(){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        int[] array = {width, height};

        return array;
    }
}
