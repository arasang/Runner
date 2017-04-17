package park.sangeun.runner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-05.
 */

public class DrawerAdapter extends BaseAdapter {

    private LayoutInflater inflater ;
    private String[] arrayTitle = {
            "내 정보",
            "기록 보기",
    };

    public DrawerAdapter(Context context){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private int[] arrayIcon = {
            R.drawable.icon_user,
            R.drawable.icon_record,
    };

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
            holder = (ViewHolder) view.getTag();
        }

        holder.imageView.setImageResource(arrayIcon[i]);
        holder.textView.setText(arrayTitle[i]);

        return view;
    }

    private class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
}
