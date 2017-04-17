package park.sangeun.runner.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import park.sangeun.runner.Fragment.GraphFragment;
import park.sangeun.runner.Fragment.UserMapFragment;

/**
 * Created by parksangeun on 2017. 4. 15..
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;
    private int id;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm, int tabCount, int id){
        super(fm);
        this.tabCount = tabCount;
        this.id = id;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                UserMapFragment mapFragment = new UserMapFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("_id", id);
                mapFragment.setArguments(bundle);
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
