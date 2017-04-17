package park.sangeun.runner.Adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-07.
 */

public class UserInfoAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Object> param = new ArrayList<Object>();
    private Handler handler;
    private LayoutInflater inflater;

    private String email = "";
    private long height = 0;
    private long weight = 0;

    public UserInfoAdapter(Context context, ArrayList<Object> param, Handler handler) {
        this.context = context;
        this.param = param;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.param = param;
        this.handler = handler;
        this.email = this.param.get(0).toString();
        this.height = (long) this.param.get(1);
        this.weight = (long) this.param.get(2);

    }

    @Override
    public int getCount() {
        return param.size();
    }

    @Override
    public Object getItem(int i) {
        return param.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_info, viewGroup, false);

            holder.textTitle = (TextView)convertView.findViewById(R.id.textTitle);
            holder.textValue = (TextView)convertView.findViewById(R.id.textValue);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        if (i == 0) {
            holder.textTitle.setText(context.getResources().getString(R.string.EmailTitle));
        } else if (i == 1) {
            holder.textTitle.setText(context.getResources().getString(R.string.HeightTitle));
        } else {
            holder.textTitle.setText(context.getResources().getString(R.string.WeightTitle));
        }

        holder.textValue.setText(param.get(i).toString());
        return convertView;
    }

    private class ViewHolder {
        TextView textTitle;
        TextView textValue;
    }
}
