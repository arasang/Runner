package park.sangeun.runner.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import park.sangeun.runner.Common.DBManager;
import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-10.
 */

public class UserWeightDialog extends AlertDialog implements View.OnClickListener{

    private LinearLayout rootLayout;
    private NumberPicker numberPicker;

    private LinearLayout linearButtons;
    private Button buttonNegative;
    private Button buttonPositive;

    private long weight = 0;
    private Handler handler;

    private ArrayList<String> arrayList = new ArrayList<String>();
    private String[] value ;

    private DBManager dbManager;

    public UserWeightDialog(Context context, long weight, Handler handler){
        super(context);

        this.weight = weight;
        this.handler = handler;

        this.dbManager = new DBManager(getContext(), Metrics.DATABASE_NAME, null ,Metrics.DATABASE_VERSION);
    }

    protected UserWeightDialog(Context context) {
        super(context);
    }

    public ArrayList<String> onReadWeight(String fileName) throws Exception{

        ArrayList<String> temp = new ArrayList<String>();

        InputStream inputStream = getContext().getAssets().open(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String value = bufferedReader.readLine();
        while (value != null) {
            value = bufferedReader.readLine();
            if(value != null)
                temp.add(value);
        }


        return temp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootLayout = new LinearLayout(getContext());
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER);
        setContentView(rootLayout);

        numberPicker = new NumberPicker(getContext());
        try {
            arrayList = onReadWeight("weight.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        value = new String[arrayList.size()];
        value = arrayList.toArray(value);


        numberPicker.setMinValue(0);
        numberPicker.setGravity(Gravity.CENTER);
        numberPicker.setMaxValue(value.length - 1);
        numberPicker.setDisplayedValues(value);

        for(int i=0; i<arrayList.size(); i++){
            if (String.valueOf(weight).equals(arrayList.get(i)))
                numberPicker.setValue(i);
        }

        linearButtons = new LinearLayout(getContext());
        linearButtons.setOrientation(LinearLayout.HORIZONTAL);
        linearButtons.setGravity(Gravity.RIGHT);

        buttonNegative = new Button(getContext());
        buttonNegative.setText(getContext().getResources().getString(android.R.string.cancel));
        buttonNegative.setBackgroundColor(Color.TRANSPARENT);
        buttonNegative.setTextColor(Color.parseColor("#FF4081"));

        buttonPositive = new Button(getContext());
        buttonPositive.setText(getContext().getResources().getString(android.R.string.ok));
        buttonPositive.setBackgroundColor(Color.TRANSPARENT);
        buttonPositive.setTextColor(Color.parseColor("#FF4081"));

        linearButtons.addView(buttonNegative, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearButtons.addView(buttonPositive, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        buttonPositive.setOnClickListener(this);
        buttonNegative.setOnClickListener(this);

        rootLayout.addView(numberPicker, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootLayout.addView(linearButtons, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    @Override
    public void onClick(View view) {
        if (view == buttonPositive) {
            String weight = value[numberPicker.getValue()];
            HashMap<String,String> paramsUpdate = new HashMap<String,String>();
            HashMap<String,String> paramsWhere = new HashMap<String,String>();

            paramsUpdate.put("USER_WEIGHT", weight);
            paramsWhere.put("_id", "1");

            try {
                Bundle bundle = new Bundle();
                bundle.putString("weight", weight);

                Message msg = new Message();
                msg.what = Metrics.DB_UPDATE_SUCCESS;
                msg.setData(bundle);
                handler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(Metrics.DB_UPDATE_FAILED);
            }

        }

        if (view == buttonNegative) {
            dismiss();
        }
    }
}
