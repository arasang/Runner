package park.sangeun.runner.Common;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2017-04-13.
 */

public class CalculateCalories {
    private double MET = 0;
    private double calories = 0;
    private Context context;
    private HashMap<String,Double> hashValue = new HashMap<String,Double>();

    private int pastValue = 0;

    public CalculateCalories(Context context){
        this.context = context;
        getMetList();
    }

    public void getMetList(){
        try {
            InputStream stream = context.getAssets().open("met_list");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String value = reader.readLine();
            while (value != null) {
                value = reader.readLine();

                if (value != null) {
                    hashValue.put(value.split(";")[0],Double.valueOf(value.split(";")[1]));
                }
            }
            Log.d("상은", "hashValue : " + hashValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double onCalculate(long weight, long time, String v, String activation){
        double velocity = Double.parseDouble(v);
        double kmPerSec = velocity * 1000 / 3600;
        switch (activation) {
            case Metrics.ACTIVITY_WALK:
                if (kmPerSec <= 1) {
                    MET = 0;
                } else if (kmPerSec < 5) {
                    MET = hashValue.get("WS");
                } else if (kmPerSec >= 5 || kmPerSec <6.4){
                    MET = hashValue.get("WM");
                } else {
                    MET = hashValue.get("WF");
                }
                break;

            case Metrics.ACTIVITY_RUN:
                MET = hashValue.get("RUN");
                break;

            case Metrics.ACTIVITY_BICYCLE:
                MET = hashValue.get("BICYCLE");
                break;
        }


        Log.d("상은", "time : " + time);
        calories = MET * weight * time/1000/3600 * 1.05;

        return calories;
    }
}
