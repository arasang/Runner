package park.sangeun.runner.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.IOException;
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

public class UserHeightDialog extends AlertDialog implements View.OnClickListener{

    private NumberPicker picker;
    private EditText editPicker;
    private Button buttonPositive;
    private Button buttonNegative;

    private long userHeight=0;
    private ArrayList<String> arrayHeight = new ArrayList<String>();
    private String[] height;

    private String selectedHeight = "";

    private DBManager dbManager;
    private Handler handler;

    public UserHeightDialog(Context context) {
        super(context);
    }

    public UserHeightDialog(Context context, long userHeight, Handler handler){
        super(context);
        this.userHeight = userHeight;
        this.handler = handler;
        this.dbManager = new DBManager(getContext(), Metrics.DATABASE_NAME, null ,Metrics.DATABASE_VERSION);
    }

    private EditText findInput(ViewGroup np) {
        int count = np.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = np.getChildAt(i);
            if (child instanceof ViewGroup) {
                findInput((ViewGroup) child);
            } else if (child instanceof EditText) {
                return (EditText) child;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_height);

        picker = (NumberPicker) findViewById(R.id.heightPicker);
        editPicker = findInput(picker);
        editPicker.setInputType(InputType.TYPE_CLASS_NUMBER);

        buttonPositive = (Button) findViewById(R.id.buttonPositive);
        buttonNegative = (Button) findViewById(R.id.buttonNegative);

        buttonPositive.setOnClickListener(this);
        buttonNegative.setOnClickListener(this);


        try {
            onReadTextFile("height.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        height = new String[arrayHeight.size()];
        height = arrayHeight.toArray(height);


        picker.setDisplayedValues(height);
        picker.setMinValue(0);
        picker.setMaxValue(height.length-1);

        for(int i=0; i<height.length; i++){
            if (Long.parseLong(height[i]) == userHeight) {
                Log.d("상은", "i : " + i);
                picker.setValue(i);
                break;
            }
        }

    }


    private void onReadTextFile(String fileName) throws IOException {
        String value;

        InputStream inputStream = getContext().getAssets().open(fileName);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        value = bufferedReader.readLine();

        while(value!=null){
            value = bufferedReader.readLine();
            if(value != null)
                arrayHeight.add(value);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonPositive:
                selectedHeight = height[picker.getValue()];
                HashMap<String,String> paramsUpdate = new HashMap<String,String>();
                HashMap<String,String> paramsWhere  = new HashMap<String,String>();

                paramsUpdate.put("USER_HEIGHT", selectedHeight);
                paramsWhere.put("_id", "1");
                try {
                    Bundle bundle = new Bundle();
                    Message msg = new Message();

                    bundle.putString("height", selectedHeight);

                    msg.what = Metrics.DB_UPDATE_SUCCESS;
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(Metrics.DB_UPDATE_FAILED);
                }
                dismiss();
                break;

            case R.id.buttonNegative:
                dismiss();
                break;
        }
    }
}
