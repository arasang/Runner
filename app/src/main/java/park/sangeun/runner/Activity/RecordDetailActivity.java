package park.sangeun.runner.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.MapFragment;

import park.sangeun.runner.Adapter.ViewPagerAdapter;
import park.sangeun.runner.Common.ConvertPixel;
import park.sangeun.runner.R;

/**
 * Created by parksangeun on 2017. 4. 15..
 */
public class RecordDetailActivity extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private int position = 0;

    private ConvertPixel convertPixel = new ConvertPixel(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        position = getIntent().getIntExtra("_id", 0);

        initView();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_map));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_graph));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setSelectedTabIndicatorHeight(convertPixel.DpToPx(3));

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), position);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tabLayout.getSelectedTabPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
