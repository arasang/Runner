package park.sangeun.runner.Activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import park.sangeun.runner.Adapter.DrawerAdapter;
import park.sangeun.runner.Common.Animation.ButtonAnimation;
import park.sangeun.runner.Common.CalculateCalories;
import park.sangeun.runner.Common.DBManager;
import park.sangeun.runner.Common.RoundingDrawable;
import park.sangeun.runner.Dialogs.ActivationDialog;
import park.sangeun.runner.Service.LocationService;
import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.R;

/**
 * Created by user on 2017-03-30.
 */

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_RESOLVE_ERROR = 2;

    private MenuItem menuActivity;
    private DrawerLayout drawerLayout;
    private TextView textTime;
    private TextView textCalories;
    private TextView textDistance;
    private TextView textVelocity;
    private GoogleMap map;
    private Button buttonStart;
    private MapFragment mapFragment;

    //DrawerLayout
    private ListView listDrawer;
    private LinearLayout linearMyPage;
    private DrawerAdapter drawerAdapter;
    private ImageView imageProfile;
    private TextView textName;
    private TextView textTotalDistance;

    private LinearLayout linearLock;
    private Button buttonPause;
    private Button buttonStop;
    private RelativeLayout buttonLock;
    private ImageView imageLock;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest.Builder builder;

    private ArrayList<Location> arrayLocations = new ArrayList<Location>();
    private ArrayList<LatLng> arrayLatLng = new ArrayList<LatLng>();
    private ArrayList<Double> arrayVelocity = new ArrayList<Double>();

    private double previousAltitude = 0;
    private double highestAltitude = 0;
    private double lowestAltitude = 0;

    private long betweenTime = 0;
    private double distance = 0;
    private long timeForCalories = 0;
    private String velocity="";
    private double totalDistance = 0;

    private boolean isStart = false;
    private boolean isLock = true;
    private boolean isBind = false;

    public static String activation = "RUN";

    private DBManager dbManager;
    private HashMap<String,Object> userInfo = new HashMap<String,Object>();
    private ArrayList<HashMap<String,Object>> userRecord = new ArrayList<HashMap<String,Object>>();
    private Bitmap profileImage = null;
    private long weight = 0;

    private Intent bindIntent;

    private ButtonAnimation buttonAnimation = new ButtonAnimation(this);
    private CalculateCalories calories;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (arrayLocations.size() > 1) {
                    Location beforeLocation = arrayLocations.get(arrayLocations.size()-2);
                    Location currentLocation = arrayLocations.get(arrayLocations.size()-1);

                    double currentAltitude = currentLocation.getAltitude();
                    if (previousAltitude < currentAltitude) {
                        highestAltitude = currentAltitude;
                    } else {
                        highestAltitude = previousAltitude;
                    }

                    previousAltitude = currentAltitude;

                    if (arrayLocations.size() != 0) {
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(arrayLocations.get(0).getLatitude(), arrayLocations.get(0).getLongitude()))
                                .title("시작 지점"));

                        if(arrayLocations.size() == 1)
                            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
                    }

                    map.addPolyline(new PolylineOptions()
                            .color(R.color.wallet_highlighted_text_holo_light)
                            .width(5)
                            .addAll(arrayLatLng));
                    LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 19));

                    double tempTime = (currentLocation.getTime() - beforeLocation.getTime()) * 0.001;

                    velocity = String.format("%.2f", (beforeLocation.distanceTo(currentLocation) / tempTime));
                    if (!velocity.equals(Double.NaN) )
                        textVelocity.setText(velocity);
                    textDistance.setText(String.format("%.1f", distance));

                    arrayVelocity.add(beforeLocation.distanceTo(currentLocation) / tempTime);

                    int cal = (int) calories.onCalculate(weight, timeForCalories, velocity, activation);
                    LocationService.calories += cal;
                    textCalories.setText(String.valueOf(LocationService.calories));

                } else if (arrayLocations.size() == 1){
                    textDistance.setText("0");
                    textVelocity.setText("0");
                    Location firstLocation = arrayLocations.get(0);
                    LatLng latlng = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());

                    map.addMarker(new MarkerOptions().position(latlng));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 19));
                }
            }

            if (msg.what == 1) {
                textTime.setText(String.format("%02d:%02d:%02d", betweenTime/1000/3600, (betweenTime/1000)/60, (betweenTime/1000)%60));
            }

            if (msg.what == Metrics.ACTIVITY_SELECT) {
                Bundle bundle = msg.getData();
                activation = bundle.getString("activity");

                switch (activation) {
                    case Metrics.ACTIVITY_RUN:
                        menuActivity.setIcon(R.drawable.icon_run_w);
                        break;

                    case Metrics.ACTIVITY_WALK:
                        menuActivity.setIcon(R.drawable.icon_walk_w);
                        break;

                    case Metrics.ACTIVITY_BICYCLE:
                        menuActivity.setIcon(R.drawable.icon_bicycle_w);
                        break;
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLocationPermission();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_menu);
        setBroadCastReceiver();
    }

    private void checkLocationPermission(){
        // 위치 정보에 대한 권한이 없을 경우
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //최초 권한 요청인가? 혹은 사용자에 의한 재요청인가 ?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //사용자가 권한을 취소한 경우
                //재요청
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, Metrics.PERMISSION_LOCATION);
            } else {
                // 최초 요청
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, Metrics.PERMISSION_LOCATION);

            }
        } else {
            initView();
            onMapSetting();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuActivity = (MenuItem) menu.findItem(R.id.actionActivation);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }

        if (id == R.id.actionActivation) {
            ActivationDialog dialog = new ActivationDialog(MainActivity.this, handler);
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUserInformation(){
        dbManager = new DBManager(this, Metrics.DATABASE_NAME, null, Metrics.DATABASE_VERSION);
        String[] paramSelect = {
                "USER_IMAGE",
                "USER_NAME",
                "USER_FIRST_NAME",
                "USER_GIVEN_NAME",
                "USER_EMAIL",
                "USER_SEX",
                "USER_AGE",
                "USER_HEIGHT",
                "USER_WEIGHT",
                "ANNUAL_GOAL",
        };
        HashMap<String,String> paramWhere = new HashMap<String,String>();
        paramWhere.put("_id", "1");
        try {
            userInfo = dbManager.onSelectUser(Metrics.DATABASE_USER_TABLE_NAME, paramSelect, paramWhere);

            byte[] image = (byte[]) userInfo.get("USER_IMAGE");
            profileImage = BitmapFactory.decodeByteArray(image, 0, image.length);
            RoundingDrawable roundingDrawable = new RoundingDrawable(profileImage);
            imageProfile.setImageDrawable(roundingDrawable);

            textName.setText((String) userInfo.get("USER_NAME"));
            textTotalDistance.setText(String.format("%.1f",totalDistance / 1000) + "km / " + String.valueOf((long)userInfo.get("ANNUAL_GOAL")) + "km");
            weight = (long) userInfo.get("USER_WEIGHT");

        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.GetDatabaseFail), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void onMapSetting() {
        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        if (status.hasResolution()) {
                            try {
                                status.startResolutionForResult(MainActivity.this, REQUEST_RESOLVE_ERROR);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // 설정이 안 되어 있고 코드로 고칠 수 없는 경우우
                        break;
                }
            }
        });
    }

    private void initView() {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        textTime = (TextView) findViewById(R.id.textTime);
        textCalories = (TextView) findViewById(R.id.textCalories);
        textDistance = (TextView) findViewById(R.id.textDistance);
        textVelocity = (TextView) findViewById(R.id.textVelocity);

        buttonStart = (Button) findViewById(R.id.buttonStart);

        linearLock = (LinearLayout) findViewById(R.id.linearLock);
        buttonLock = (RelativeLayout) findViewById(R.id.buttonLock);
        imageLock = (ImageView) findViewById(R.id.imageLock);
        buttonPause = (Button) findViewById(R.id.buttonPause);
        buttonStop = (Button)findViewById(R.id.buttonStop);

        listDrawer = (ListView) findViewById(R.id.listDrawer);
        linearMyPage = (LinearLayout) findViewById(R.id.linearMyPage);
        imageProfile = (ImageView) findViewById(R.id.imageProfile);
        textName = (TextView) findViewById(R.id.textName);
        textTotalDistance = (TextView) findViewById(R.id.textTotalDistance);

        drawerAdapter = new DrawerAdapter(this);

        listDrawer.setAdapter(drawerAdapter);
        listDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        //TODO: MyPage
                        Intent myintent = new Intent(MainActivity.this, MyPageActivity.class);
                        startActivity(myintent);

                        break;

                    case 1:
                        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                        startActivity(intent);
                        break;
                }
                drawerLayout.closeDrawers();
            }
        });

        mapFragment.getMapAsync(this);
        linearMyPage.setOnClickListener(this);
        buttonStart.setOnClickListener(this);
        buttonLock.setOnClickListener(this);
        buttonPause.setOnClickListener(this);
        buttonStop.setOnClickListener(this);

        getUserInformation();
        getRecordDetail();

    }

    private void setBroadCastReceiver() {
        IntentFilter filterLocation = new IntentFilter("LOCATION");
        registerReceiver(receiver, filterLocation);

        IntentFilter filterTime = new IntentFilter("TIME");
        registerReceiver(receiver, filterTime);

        IntentFilter filterCurrent = new IntentFilter("CURRENTLOCATION");
        registerReceiver(receiver, filterCurrent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case Metrics.PERMISSION_LOCATION :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO: 현재위치 가져오기
                    initView();
                } else {
                    final AlertDialog dialog = new AlertDialog.Builder(this).create();
                    dialog.setMessage("위치 정보 기능을 활성화해야 앱을 원활하게 사용하실 수 있습니다.");
                    dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                            getResources().getString(android.R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == linearMyPage) {
            Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
            startActivity(intent);
        }

        if (view == buttonLock) {
            buttonAnimation.onButtonBounce(buttonLock);

            if (isLock) {
                imageLock.setImageResource(R.drawable.icon_unlock);
                buttonLock.setBackgroundResource(R.drawable.background_button_circle);
                isLock = false;

                buttonAnimation.onButtonOn(buttonStop,R.color.colorRed);
                buttonAnimation.onButtonOn(buttonPause, R.color.colorYellow);

            } else {
                imageLock.setImageResource(R.drawable.icon_lock);
                buttonLock.setBackgroundResource(R.drawable.background_button_lock);
                isLock = true;

                buttonAnimation.onButtonOff(buttonStop,R.color.colorRed);
                buttonAnimation.onButtonOff(buttonPause, R.color.colorYellow);
            }
        }

        if (view == buttonPause) {
            if (isLock) {
                buttonAnimation.onButtonBounce(buttonLock);
            } else {
                locationService.onPause();
            }
        }

        if (view == buttonStop) {
            if (isLock) {
                buttonAnimation.onButtonBounce(buttonLock);
            } else {
                if (isStart) {
                    isLock = true;
                    imageLock.setImageResource(R.drawable.icon_lock);
                    buttonLock.setBackgroundResource(R.drawable.background_button_lock);

                    buttonLock.setVisibility(View.GONE);
                    linearLock.setVisibility(View.GONE);


                    unbindService(serviceConnection);
                    isBind = false;

                    Intent locationIntent = new Intent(MainActivity.this, LocationService.class);
                    stopService(locationIntent);


                    isStart = false;
                    handler.removeMessages(0);

                    final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                    dialog.setTitle("기록 완료");
                    dialog.setMessage("기록을 저장할까요 ?");
                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();

                            textDistance.setText("0");
                            textTime.setText("00:00:00");
                            textCalories.setText("0");
                            textVelocity.setText("0");
                            map.clear();
                        }
                    });

                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "저장", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DBManager dbManager = new DBManager(MainActivity.this, Metrics.DATABASE_NAME, null, Metrics.DATABASE_VERSION);
                            StringBuilder query = new StringBuilder();

                            long currentDate = System.currentTimeMillis();

                            Date date = new Date(currentDate);
                            SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd");
                            String CurrentDate = DateFormat.format(date);

                            query.append("INSERT INTO RECORD_TABLE(ACTIVITY,DISTANCE,TIME,CALORIES,RECORD_DATE) VALUES(");
                            query.append("'" + activation + "'" + ",");
                            query.append(String.valueOf(distance) + ",");
                            query.append(String.valueOf(betweenTime) + ",");
                            query.append("0,"); // 칼로리가 들어가야되는데 ...!
                            query.append("'" + CurrentDate + "'"); // 날짜

                            query.append(");");

                            dbManager.onInsert(query.toString());

                            dialog.dismiss();
                            map.clear();

                            double totalVelocity = 0;
                            double highestVelocity = 0;
                            if (arrayVelocity.size() > 1) {
                                for (int j=0; j<arrayVelocity.size(); j++) {
                                    totalVelocity += arrayVelocity.get(j);

                                    if(highestVelocity < arrayVelocity.get(j))
                                        highestVelocity = arrayVelocity.get(j);
                                }
                            } else if (arrayVelocity.size() == 1) {
                                totalVelocity = arrayVelocity.get(0);
                                highestVelocity = arrayVelocity.get(0);
                            }

                            query = new StringBuilder();
                            query.append(
                                    "INSERT INTO RECORD_DETAIL(" +
                                            "START_LATITUDE, " +
                                            "START_LONGITUDE, " +
                                            "END_LATITUDE, " +
                                            "END_LONGITUDE, " +
                                            "HIGHEST_ALTITUDE, " +
                                            "LOWEST_ALTITUDE, " +
                                            "AVG_SPEED, " +
                                            "HIGHEST_SPEED, " +
                                            "TOTAL_CALORIES, " +
                                            "TOTAL_TIME) VALUES(");
                            query.append(arrayLocations.get(0).getLatitude() + ", ");
                            query.append(arrayLocations.get(0).getLongitude() + ", ");
                            query.append(arrayLocations.get(arrayLocations.size()-1).getLatitude() + ", ");
                            query.append(arrayLocations.get(arrayLocations.size()-1).getLongitude() + ", ");
                            query.append(String.valueOf(highestAltitude) + ", ");
                            query.append(String.valueOf(lowestAltitude) + ", ");
                            query.append(String.valueOf(totalVelocity) + ", ");
                            query.append(String.valueOf(highestVelocity) + ", ");
                            query.append(String.valueOf(LocationService.calories) + ", ");
                            query.append(String.valueOf(betweenTime));
                            Log.d("상은", "betweenTime : " + betweenTime);
                            query.append(");");

                            dbManager.onInsert(query.toString());

                            arrayLatLng.clear();
                            arrayLocations.clear();
                            arrayVelocity.clear();

                        }
                    });

                    buttonStart.setVisibility(View.VISIBLE);

                    dialog.show();
                }
            }
        }

        if (view == buttonStart) {
            if (!isStart) {
                isStart = true;
                if (!isBind) {
                    doBindService();
                }

                Intent locationIntent = new Intent(MainActivity.this, LocationService.class);
                startService(locationIntent);

                linearLock.setVisibility(View.VISIBLE);
                buttonLock.setVisibility(View.VISIBLE);
                buttonStart.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                )
        {return;}
        map.setMyLocationEnabled(true);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 0);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("LOCATION".equals(intent.getAction())) {
                arrayLocations = intent.getParcelableArrayListExtra("arrayLocation");
                arrayLatLng = intent.getParcelableArrayListExtra("arrayLatLng");
                distance = intent.getDoubleExtra("distance", 0);
                timeForCalories = intent.getLongExtra("timeForCalories", 0);
                handler.sendEmptyMessage(0);
            }

            if ("TIME".equals(intent.getAction())) {
                betweenTime = intent.getLongExtra("time", 0);
                handler.sendEmptyMessage(1);
            }

            if ("LOCATIONCURRENT".equals(intent.getAction())) {
                ArrayList<Location> current = intent.getParcelableArrayListExtra("arrayLocation");

                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(current.get(0).getLatitude(), current.get(0).getLongitude())));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        calories = new CalculateCalories(getApplicationContext());
        isStart = checkServiceRunning();
        getUserInformation();
    }

    private boolean checkServiceRunning(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                buttonStart.setVisibility(View.GONE);
                buttonLock.setVisibility(View.VISIBLE);
                linearLock.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return false;
    }

    void doBindService(){
        bindIntent = new Intent(MainActivity.this, LocationService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        isBind = true;
    }

    private LocationService locationService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            locationService = ((LocationService.LocationServiceBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind) {
            unbindService(serviceConnection);
            isBind = false;
        }
    }

    private void getRecordDetail(){
        String[] paramsSelect = {
                "DISTANCE",
        };

        HashMap<String, String> paramsWhere = new HashMap<String, String>();

        try {
            userRecord = dbManager.onSelectRecord(Metrics.DATABASE_RECORD_TABLE_NAME, paramsSelect, paramsWhere);

            for(int i=0; i<userRecord.size(); i++) {
                HashMap<String,Object> value = userRecord.get(i);
                String value2 = (String)value.get("DISTANCE");
                totalDistance += Double.parseDouble(value2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
