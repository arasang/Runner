package park.sangeun.runner.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import park.sangeun.runner.Adapter.RecordDetailAdapter;
import park.sangeun.runner.Common.DBManager;
import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.R;

/**
 * Created by parksangeun on 2017. 4. 15..
 */

public class UserMapFragment extends Fragment implements OnMapReadyCallback,  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private View view ;
    private GoogleMap map;
    private MapFragment mapFragment;
    private TextView textTitle;
    private TextView textDistance;
    private TextView textTime;
    private TextView textCalories;
    private ListView listView;

    private RecordDetailAdapter adapter;

    private ArrayList<HashMap<String,Object>> resultRecord = new ArrayList<HashMap<String,Object>>();
    private DBManager dbManager;
    private int position=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);

        initView();

        return view;
    }

    private void initView(){
        Log.d("상은", "initView");
        mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        textTitle = (TextView) view.findViewById(R.id.textActivityTitle);
        textDistance = (TextView) view.findViewById(R.id.textDistance);
        textTime = (TextView) view.findViewById(R.id.textTime);
        textCalories = (TextView) view.findViewById(R.id.textCalories);
        listView = (ListView) view.findViewById(R.id.listView);

        try {
            resultRecord = getDataFromDB();
            Log.d("상은", "resultRecord : " + resultRecord);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("오류");
            alertDialog.setMessage("데이터를 불러올 수 없습니다. 관리자에게 문의하세요.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getActivity().finish();
                }
            });
        }

        setListViewHeightBasedOnChildren(listView);

        listView.setFocusable(false);
        
        long time = Long.parseLong(resultRecord.get(0).get("TOTAL_TIME").toString());

        textTitle.setText(setActivityTitle());
        if (resultRecord.get(0).get("TOTAL_DISTANCE").toString() == null) {
            textDistance.setText("0");
        }else {
            textDistance.setText(resultRecord.get(0).get("TOTAL_DISTANCE").toString());
        }
        textTime.setText(String.format("%02d:%02d:%02d", time/1000/3600, (time/1000)/60, (time/1000)%60));
        textCalories.setText(resultRecord.get(0).get("TOTAL_CALORIES").toString());
    }

    private String setActivityTitle() {
        String value = "";

        switch (resultRecord.get(0).get("ACTIVATION").toString()){
            case Metrics.ACTIVITY_RUN:
                value = getResources().getString(R.string.Run);
                break;

            case Metrics.ACTIVITY_WALK:
                value = getResources().getString(R.string.Walk);
                break;

            case Metrics.ACTIVITY_BICYCLE:
                value = getResources().getString(R.string.Bicycle);
                break;
        }
        return value;
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        adapter = new RecordDetailAdapter(getContext(), resultRecord);
        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

        listView.setAdapter(adapter);
    }



    private ArrayList<HashMap<String,Object>> getDataFromDB() throws Exception {
        ArrayList<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();
        dbManager = new DBManager(getActivity(), Metrics.DATABASE_NAME, null, Metrics.DATABASE_VERSION);
        String[] paramsSelect = {
                "ACTIVATION",
                "START_LATITUDE",
                "START_LONGITUDE",
                "END_LATITUDE",
                "END_LONGITUDE",
                "HIGHEST_ALTITUDE",
                "LOWEST_ALTITUDE",
                "AVG_SPEED",
                "HIGHEST_SPEED",
                "TOTAL_DISTANCE",
                "TOTAL_CALORIES",
                "TOTAL_TIME",
        };
        HashMap<String,String> paramsWhere = new HashMap<String,String>();
        paramsWhere.put("_id", String.valueOf(position));
        result = dbManager.onSelectRecord(Metrics.DATABASE_RECORD_DETAIL_TABLE_NAME, paramsSelect, paramsWhere);

        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        Log.d("상은", "onCreate");
        position = bundle.getInt("_id");
        Log.d("상은", "position : " + position);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("상은", "언제 생성 되는거냐 ");
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng startPosition = new LatLng(Double.parseDouble((String)resultRecord.get(0).get("START_LATITUDE")), Double.parseDouble((String)resultRecord.get(0).get("START_LONGITUDE")));
        LatLng endPosition = new LatLng(Double.parseDouble((String)resultRecord.get(0).get("END_LATITUDE")), Double.parseDouble((String)resultRecord.get(0).get("END_LONGITUDE")));


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(endPosition, 15));
        map.addMarker(new MarkerOptions()
        .title("시작지점입니다.")
        .position(startPosition));

        map.addMarker(new MarkerOptions()
        .title("종료지점입니다.")
        .position(endPosition));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
