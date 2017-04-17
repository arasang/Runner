package park.sangeun.runner.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-04.
 */

public class RecordAdapter extends CursorAdapter {

    public RecordAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        Log.d("상은", "cursor : " + c);
    }

    public RecordAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_record, viewGroup, false);


        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageRecord = (ImageView) view.findViewById(R.id.imageRecord);
        TextView textDistance = (TextView)view.findViewById(R.id.textDistance);
        TextView textTime = (TextView)view.findViewById(R.id.textTime);
        TextView textDate = (TextView)view.findViewById(R.id.textDate);

        switch(cursor.getString(1)){
            case Metrics.ACTIVITY_RUN:
                imageRecord.setImageResource(R.drawable.icon_run);
                break;

            case Metrics.ACTIVITY_WALK:
                imageRecord.setImageResource(R.drawable.icon_walk);
                break;

            case Metrics.ACTIVITY_BICYCLE:
                imageRecord.setImageResource(R.drawable.icon_bicycle);
                break;
        }


        textDistance.setText(String.format("%.2f",Double.parseDouble(cursor.getString(2)) / 1000) + "km");

        long time = cursor.getLong(3);
        textTime.setText(String.format("%02d:%02d:%02d", time/1000/3600, time/1000/60, time/1000%60));
        String date = cursor.getString(4);
        String year = date.substring(0,4);
        String month = date.substring(4,6);
        String day = date.substring(6,8);

        textDate.setText(year + "/" + month + "/" + day);
        Log.d("상은", "Cursor 4 : " + cursor.getString(4));
    }
}
