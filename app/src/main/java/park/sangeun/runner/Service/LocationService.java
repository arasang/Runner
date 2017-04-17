package park.sangeun.runner.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import park.sangeun.runner.Activity.MainActivity;
import park.sangeun.runner.R;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient client;
    private LocationRequest request;
    private LocationSettingsRequest.Builder builder;

    private static long betweenTime = 0;

    private static long startTime = 0;
    private static long baseTime = 0;
    private static long pauseTime = 0;

    public static int calories = 0;

    private static ArrayList<Location> arrayLocation = new ArrayList<Location>();
    private static ArrayList<LatLng> arrayLatLng = new ArrayList<LatLng>();

    private Location beforeLocation;
    private Location currentLocation;
    private double distance;

    //Notification
    private NotificationManager manager;
    private int NOTIFICATION = R.string.local_service_started;
    private Notification.Builder notificationBuilder;
    private Notification notification;

    private boolean isPause = false;

    public class LocationServiceBinder extends Binder {
        public LocationService getService(){
            return LocationService.this;
        }
    }
    public final IBinder binder = new LocationServiceBinder();

    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    private Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Location location = LocationServices.FusedLocationApi.getLastLocation(client);
                Intent intent = new Intent("LOCATION");

                if (location != null) {
                    arrayLocation.add(location);
                    arrayLatLng.add(new LatLng(location.getLatitude(), location.getLongitude()));

                    if (arrayLocation.size() > 1) {
                        beforeLocation = arrayLocation.get(arrayLocation.size()-2);
                        currentLocation = arrayLocation.get(arrayLocation.size()-1);

                        distance += beforeLocation.distanceTo(currentLocation);
                        intent.putExtra("distance", distance);
                        intent.putExtra("timeForCalories", (long) beforeLocation.distanceTo(currentLocation));
                    }
                }

                intent.putParcelableArrayListExtra("arrayLocation", arrayLocation);
                intent.putParcelableArrayListExtra("arrayLatLng", arrayLatLng);
                sendBroadcast(intent);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                }, 1000);
            }

            if (msg.what == 1) {
                long now = SystemClock.elapsedRealtime();
                betweenTime = now - baseTime;

                Intent intent = new Intent("TIME");
                intent.putExtra("time", betweenTime);
                sendBroadcast(intent);

                handler.sendEmptyMessage(1);
            }
        }
    };

    public LocationService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification(){
        CharSequence text = String.format("%02d:%02d:%02d", betweenTime/1000/3600, (betweenTime/1000)/60, (betweenTime/1000)%60);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notificationBuilder = new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.icon_record)
                .setContentTitle("기록중입니다.")
                .setContentText(text)
                .setTicker("측정을 시작합니다.");
        notification = notificationBuilder.build();

        // Send the notification.
        manager.notify(NOTIFICATION, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        baseTime = SystemClock.elapsedRealtime();
        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initLocation(){
        client = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
        request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        request.setExpirationDuration(1000);
        request.setExpirationTime(1000);

        builder = new LocationSettingsRequest.Builder().addLocationRequest(request);
        builder.setAlwaysShow(true);

        startLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(arrayLocation.size() == 0) {
            handler.sendEmptyMessage(1);
            showNotification();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //시작 !
    private void startLocation(){
        client.connect();
        startTime = SystemClock.elapsedRealtime();
        handler.sendEmptyMessage(0);
    }

    private void stopLocation(){
        arrayLatLng.clear();
        arrayLocation.clear();
        calories = 0;
        handler.removeMessages(0);
        handler.removeMessages(1);
        if(client != null)
            client.disconnect();
    }

    @Override
    public void onDestroy() {
        stopLocation();
        manager.cancel(NOTIFICATION);
        super.onDestroy();
    }

    public void onPause(){
        if (!isPause) {
            pauseTime = SystemClock.elapsedRealtime();
            handler.removeMessages(0);
            handler.removeMessages(1);
            isPause = true;
        } else {
            long now = SystemClock.elapsedRealtime();
            baseTime += (now - pauseTime);
            handler.sendEmptyMessage(0);
            handler.sendEmptyMessage(1);
            isPause = false;
        }
    }
}
