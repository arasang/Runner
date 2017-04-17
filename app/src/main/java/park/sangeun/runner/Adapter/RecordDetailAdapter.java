package park.sangeun.runner.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-17.
 */

public class RecordDetailAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private int[] stringValues = {
            R.string.AverageVelocity,
            R.string.MaxVelocity,
            R.string.RisingAltitude,
            R.string.DescendingAltitude,
            R.string.MaxAltitude,
    };

    private int[] imageValues = {
            R.drawable.icon_velocity,
            R.drawable.icon_velocity,
            R.drawable.icon_altitude_up,
            R.drawable.icon_altitude_min,
            R.drawable.icon_mountain,
    };
    private HashMap<String,Object> value = new HashMap<String,Object>();

    public RecordDetailAdapter(Context context, ArrayList<HashMap<String,Object>> param){
        this.context = context;
        value = param.get(0);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.item_record_detail, viewGroup, false);

            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.textTitle = (TextView) convertView.findViewById(R.id.textTitle);
            holder.textValue = (TextView) convertView.findViewById(R.id.textValue);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Log.d("상은", "position : " + position);

        holder.imageView.setImageResource(imageValues[position]);
        holder.textTitle.setText(context.getResources().getString(stringValues[position]));

        holder.textValue.setText(whichOne(position, value));

        return convertView;
    }

    private String whichOne(int position, HashMap<String,Object> param){
        String value = "";
        switch (position) {
            case 0:
                String totalVelocity = param.get("AVG_SPEED").toString();
                String totalTime = param.get("TOTAL_TIME").toString();

                long time = Long.parseLong(totalTime) / 1000;
                long velocity = Long.parseLong(totalVelocity);

                value = String.valueOf(velocity / time);

                break;

            case 1:
                value = param.get("HIGHEST_SPEED").toString() + "km/h";
                break;

            case 2:
                value = param.get("HIGHEST_ALTITUDE").toString() + "m";
                break;

            case 3:
                value = param.get("LOWEST_ALTITUDE").toString() + "m";
                break;

            case 4:
                value = param.get("HIGHEST_ALTITUDE").toString() + "m";
                break;

        }

        return value;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textTitle;
        TextView textValue;
    }
}
