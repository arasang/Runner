package park.sangeun.runner.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import park.sangeun.runner.Fragment.GraphFragment;
import park.sangeun.runner.Fragment.MapFragment;

/**
 * Created by parksangeun on 2017. 4. 15..
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm, int tabCount){
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                MapFragment mapFragment = new MapFragment();
                return mapFragment;

            case 1:
                GraphFragment graphFragment = new GraphFragment();
                return graphFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
