package park.sangeun.runner.Adapter;

import android.content.Context;
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
 * Created by user on 2017-04-05.
 */

public class ActivationAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private String[] arrayTitle = {
            "달리기",
            "걷기",
            "자전거",
    };

    private int[] arrayIcon = {
            R.drawable.icon_run,
            R.drawable.icon_walk,
            R.drawable.icon_bicycle
    };
    public ActivationAdapter(Context context){
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return arrayTitle.length;
    }

    @Override
    public Object getItem(int i) {
        return arrayTitle[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();

            view = inflater.inflate(R.layout.item_drawer, viewGroup, false);

            holder.imageView = (ImageView)view.findViewById(R.id.imageIcon);
            holder.textView = (TextView)view.findViewById(R.id.textTitle);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        holder.textView.setText(arrayTitle[i]);
        holder.imageView.setImageResource(arrayIcon[i]);

        return view;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView textView;
    }
}
