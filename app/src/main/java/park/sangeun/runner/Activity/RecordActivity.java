package park.sangeun.runner.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;

import park.sangeun.runner.Adapter.RecordAdapter;
import park.sangeun.runner.Common.DBManager;
import park.sangeun.runner.Common.Metrics;

/**
 * Created by user on 2017-04-04.
 */

public class RecordActivity extends AppCompatActivity {

    private LinearLayout linearRoot;
    private ListView listRecord;
    private RecordAdapter adapter;

    //Database
    private DBManager dbManager;
    private Cursor cursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("운동 내역");

        linearRoot = new LinearLayout(this);
        setContentView(linearRoot);

        listRecord = new ListView(this);
        listRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(RecordActivity.this, RecordDetailActivity.class);
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });
        linearRoot.addView(
                listRecord,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        dbManager = new DBManager(this, Metrics.DATABASE_NAME, null, Metrics.DATABASE_VERSION);

        String[] paramsSelect = {"_id", "ACTIVITY", "DISTANCE", "TIME", "RECORD_DATE"};
        HashMap<String,String> paramsWhere = new HashMap<String, String>();

        try {
            cursor = dbManager.getCursor(Metrics.DATABASE_RECORD_TABLE_NAME, paramsSelect, paramsWhere);
            adapter = new RecordAdapter(this, cursor, true);
            listRecord.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        listRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Intent intent = new Intent(RecordActivity.this, RecordDetailActivity.class);
//                intent.putExtra("_id", position);
//                startActivity(intent);
                Toast.makeText(RecordActivity.this, "준비중입니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem    item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
