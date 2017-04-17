package park.sangeun.runner.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import park.sangeun.runner.Adapter.GoalAdapter;
import park.sangeun.runner.Adapter.UserInfoAdapter;
import park.sangeun.runner.Common.DBManager;
import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.Common.RoundingDrawable;
import park.sangeun.runner.Dialogs.ProfileDialog;
import park.sangeun.runner.Dialogs.UserHeightDialog;
import park.sangeun.runner.Dialogs.UserWeightDialog;
import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-07.
 */

public class MyPageActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private ImageView imageProfile;
    private EditText editFirstName;
    private EditText editGivenName;
    private ListView listUserInfo;
    private UserInfoAdapter adapter;
    private Button buttonMale;
    private Button buttonFemale;

    private ListView listGoal;
    private DBManager dbManager;
    private HashMap<String,Object> getUserInfo = new HashMap<String,Object>();
    private ArrayList<Object> arrayList;

    private Bitmap profileImage;
    private String firstName = "";
    private String givenName = "";
    private String sex = "";
    private String email = "";
    private long height = 0;
    private long weight = 0;

    private String selectedImagePath;
    private Bitmap bitmap;
    private byte[] imageByte;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case Metrics.DB_UPDATE_SUCCESS:
                    Bundle bundle = msg.getData();

                    if(bundle.getString("height")!=null)
                        height = Long.parseLong(bundle.getString("height"));
                    if(bundle.getString("weight")!=null)
                        weight = Long.parseLong(bundle.getString("weight"));
                    arrayList.clear();

                    arrayList.add(email);
                    arrayList.add(height);
                    arrayList.add(weight);

                    adapter.notifyDataSetChanged();
                    break;

                case Metrics.DB_UPDATE_FAILED:
                    Toast.makeText(MyPageActivity.this, getString(R.string.UserInfoFailed), Toast.LENGTH_SHORT).show();
                    break;


                default:
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        initView();

        getSupportActionBar().setTitle(getResources().getString(R.string.MyInformation));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageProfile = (ImageView)findViewById(R.id.imageProfile);
        editFirstName = (EditText)findViewById(R.id.editFirstName);
        editGivenName = (EditText)findViewById(R.id.editGivenName);
        buttonFemale = (Button)findViewById(R.id.buttonFemale);
        buttonMale = (Button)findViewById(R.id.buttonMale);
        listGoal = (ListView) findViewById(R.id.listGoal);

        listUserInfo = (ListView)findViewById(R.id.listInfo);

        imageProfile.setOnClickListener(this);
        buttonFemale.setOnClickListener(this);
        buttonMale.setOnClickListener(this);

        GoalAdapter goalAdapter = new GoalAdapter(this);
        listGoal.setAdapter(goalAdapter);

        listGoal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyPageActivity.this, AnnualGoalActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setUserInfo(HashMap<String,Object> param){
        byte[] image = (byte[]) param.get("USER_IMAGE");

        firstName = (String)param.get("USER_FIRST_NAME");
        givenName = (String)param.get("USER_GIVEN_NAME");

        editFirstName.setText(firstName);
        editGivenName.setText(givenName);

        email = param.get("USER_EMAIL").toString();
        sex   = param.get("USER_SEX").toString();
        height = (long)param.get("USER_HEIGHT");
        weight = (long)param.get("USER_WEIGHT");


        profileImage = BitmapFactory.decodeByteArray(image, 0, image.length);
        RoundingDrawable roundingDrawable = new RoundingDrawable(profileImage);
        imageProfile.setImageDrawable(roundingDrawable);

        Log.d("상은", "sex : " + sex);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ("M".equals(sex)) {
                buttonMale.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                buttonFemale.setTextColor(getResources().getColor(R.color.colorAccent, null));
            }
        } else {
            if ("M".equals(sex)) {
                buttonMale.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                buttonFemale.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }

        arrayList = new ArrayList<Object>();
        arrayList.add(email);
        arrayList.add(height);
        arrayList.add(weight);
        adapter = new UserInfoAdapter(this, arrayList, handler);

        listUserInfo.setAdapter(adapter);
        listUserInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        break;
                    case 1:
                        UserHeightDialog heightDialog = new UserHeightDialog(MyPageActivity.this, height, handler);
                        heightDialog.show();
                        break;

                    case 2:
                        UserWeightDialog weightDialog = new UserWeightDialog(MyPageActivity.this, weight, handler);
                        weightDialog.show();
                        break;

                    default:
                        Toast.makeText(MyPageActivity.this, "서비스 준비중입니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    //TODO: DATABASE에서 정보 가져오기
    private HashMap<String,Object> getDataInDatabase(){
        HashMap<String,Object> result = new HashMap<String,Object>();
        dbManager = new DBManager(this, Metrics.DATABASE_NAME, null, Metrics.DATABASE_VERSION);
        String[] paramSelect = {
                "USER_IMAGE",
                "USER_NAME",
                "USER_FIRST_NAME",
                "USER_GIVEN_NAME",
                "USER_EMAIL",
                "USER_AGE",
                "USER_SEX",
                "USER_HEIGHT",
                "USER_WEIGHT",
                "ANNUAL_GOAL",
        };
        HashMap<String,String> paramWhere = new HashMap<String,String>();
        paramWhere.put("_id", "1");

        try {
            result = dbManager.onSelectUser(Metrics.DATABASE_USER_TABLE_NAME, paramSelect, paramWhere);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.GetDatabaseFail), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.actionNext) {
            firstName = editFirstName.getText().toString();
            givenName = editGivenName.getText().toString();

            if (imageByte == null) {
                RoundingDrawable drawable = (RoundingDrawable)imageProfile.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                imageByte = stream.toByteArray();
            }

            ArrayList<Object> arrayList = new ArrayList<Object>();
            arrayList.add(imageByte);
            arrayList.add(firstName + givenName);
            arrayList.add(firstName);
            arrayList.add(givenName);
            arrayList.add(email);
            arrayList.add(sex);
            arrayList.add(height);
            arrayList.add(weight);

            dbManager.onUpdateProfile(arrayList);

            finish();
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == imageProfile) {
            if (checkPermissions()) {
                ProfileDialog dialog = new ProfileDialog(this);
                dialog.startDialog();
            }
        }

        if (view == buttonMale) {
            sex = "M";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                buttonMale.setTextColor(getResources().getColor(R.color.colorBlue, null));
                buttonFemale.setTextColor(getResources().getColor(android.R.color.black, null));
            } else {
                buttonMale.setTextColor(getResources().getColor(R.color.colorBlue));
                buttonFemale.setTextColor(getResources().getColor(android.R.color.black));
            }
        }

        if (view == buttonFemale) {
            sex = "W";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                buttonMale.setTextColor(getResources().getColor(android.R.color.black, null));
                buttonFemale.setTextColor(getResources().getColor(R.color.colorAccent, null));
            } else {
                buttonMale.setTextColor(getResources().getColor(android.R.color.black));
                buttonFemale.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<String>();
        for (String permission:Metrics.PERMISSION_LIST){
            result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, permissionList.toArray(
                            new String[permissionList.size()]
                    ),
                    Metrics.MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Metrics.MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(Metrics.PERMISSION_LIST[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(Metrics.PERMISSION_LIST[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(Metrics.PERMISSION_LIST[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = null;
        selectedImagePath = null;
        if (resultCode == RESULT_OK && requestCode == Metrics.CAMERA_REQUEST) {
            File f = new File(Environment.getExternalStorageDirectory().toString());

            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            if (!f.exists()) {
                Toast.makeText(this, getResources().getString(R.string.CaptureError), Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true
                );
                RoundingDrawable roundingDrawable = new RoundingDrawable(bitmap);
                imageProfile.setImageDrawable(roundingDrawable);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == Metrics.GALLERY_PICTURE) {
            if (data != null) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();

                bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                // preview image
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);

                RoundingDrawable roundingDrawable = new RoundingDrawable(bitmap);
                imageProfile.setImageDrawable(roundingDrawable);
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }

        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        imageByte = stream.toByteArray();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo = getDataInDatabase();
        setUserInfo(getUserInfo);
    }
}
