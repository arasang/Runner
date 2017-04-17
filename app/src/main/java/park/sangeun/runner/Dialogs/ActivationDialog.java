package park.sangeun.runner.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import park.sangeun.runner.Adapter.ActivationAdapter;
import park.sangeun.runner.Common.ConvertPixel;
import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-05.
 */

public class ActivationDialog extends Dialog {

    private ListView listView;
    private ActivationAdapter adapter;
    private Handler handler;
    private ConvertPixel convertPixel;

    public ActivationDialog(Context context, Handler handler) {
        super(context);
        this.handler = handler;
        convertPixel = new ConvertPixel(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = new ListView(getContext());
        adapter = new ActivationAdapter(getContext());
        listView.setAdapter(adapter);

        setContentView(listView);

        TextView textTitle = new TextView(getContext());
        textTitle.setGravity(Gravity.CENTER_VERTICAL);

        ListView.LayoutParams params = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertPixel.DpToPx(40));
        textTitle.setLayoutParams(params);
        textTitle.setText(getContext().getResources().getString(R.string.ActivationTitle));
        textTitle.setTextSize(19);
        textTitle.setTextColor(Color.BLACK);
        textTitle.setPadding(convertPixel.DpToPx(10), 0, 0, 0);

        listView.addHeaderView(textTitle);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                Message msg = new Message();
                msg.what = Metrics.ACTIVITY_SELECT;
                switch(i){
                    case 0:
                        bundle.putString("activity", "RUN");
                        break;

                    case 1:
                        bundle.putString("activity", "WALK");
                        break;

                    case 2:
                        bundle.putString("activity", "BICYCLE");
                        break;
                }
                msg.setData(bundle);
                handler.sendMessage(msg);

                dismiss();
            }
        });
    }

}
