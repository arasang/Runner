package park.sangeun.runner.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import park.sangeun.runner.Common.DBManager;
import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.Common.RoundingDrawable;
import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-14.
 */

public class AnnualGoalActivity extends AppCompatActivity {

    private TextView textGoal;
    private TextView textWeek;
    private TextView textMonth;

    private SeekBar seekBar;

    private int goal;

    private DBManager dbManager = new DBManager(this, Metrics.DATABASE_NAME, null ,Metrics.DATABASE_VERSION);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annual);
        try {
            getGoal();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.GetDatabaseFail), Toast.LENGTH_SHORT).show();
            goal = 0;
        }
        initActionBar();
        initView();
    }

    private void initActionBar() {
        getSupportActionBar().setTitle(getResources().getString(R.string.AnnualTitle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
    }

    private void getGoal() throws Exception {
        String[] paramsSelect = {
                "ANNUAL_GOAL",
        };

        HashMap<String,String> paramsWhere = new HashMap<String,String>();
        paramsWhere.put("_id", "1");
        HashMap<String, Object> result = dbManager.onSelect(Metrics.DATABASE_USER_TABLE_NAME, paramsSelect, paramsWhere);

        goal = Integer.parseInt((String)result.get("ANNUAL_GOAL"));
    }

    private void initView(){
        textGoal = (TextView)findViewById(R.id.textGoal);
        textMonth = (TextView)findViewById(R.id.textMonth);
        textWeek  = (TextView)findViewById(R.id.textWeek);

        seekBar = (SeekBar)findViewById(R.id.seek_bar);
        seekBar.setProgress(goal);

        int month = goal / 12;
        int week = month / 4;

        textGoal.setText(String.valueOf(goal) + "km");
        textMonth.setText(String.valueOf(month));
        textWeek.setText(String.valueOf(week));


        seekBar.setProgress(goal/25);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                goal = progress * 25;
                if (progress <= 2) {
                    goal = 50;
                }
                textGoal.setText(String.valueOf(goal) + "km");

                int month = goal / 12;
                int week = month / 4;

                textMonth.setText(String.valueOf(month));
                textWeek.setText(String.valueOf(week));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionNext) {
           dbManager.onUpdateGoal(goal);
            finish();
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
