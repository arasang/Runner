package park.sangeun.runner.Activity;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import park.sangeun.runner.Common.DBManager;
import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.Fragment.UserInfoFragment;
import park.sangeun.runner.Fragment.UserNameFragment;
import park.sangeun.runner.R;

/**
 * Created by user on 2017-03-30.
 */

public class LoginActivity extends AppCompatActivity{

    private UserNameFragment userNameFragment;
    private UserInfoFragment userInfoFragment;

    private String userName;
    private String userFirstName;
    private String userGivenName;
    private String userEmail;
    private long userAge;
    private String userSex;
    private long userHeight;
    private long userWeight;
    private byte[] imageByte;

    private Drawable image;

    private DBManager dbManager = new DBManager(this, Metrics.DATABASE_NAME, null, Metrics.DATABASE_VERSION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameFragment = new UserNameFragment();
        userInfoFragment = new UserInfoFragment();

        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.ani_slide_in, R.animator.ani_slide_out).replace(android.R.id.content, userNameFragment, "USERNAME").addToBackStack("USERNAME").commit();
    }

    public void goUserInfo(byte[] imageByte, String email, String name, String firstName, String givenName, int age, String sex){
        if (getFragmentManager().findFragmentByTag("USERNAME") == userNameFragment) {

            this.imageByte = imageByte;
            this.userEmail = email;
            this.userName = name;
            this.userFirstName = firstName;
            this.userGivenName = givenName;
            this.userAge  = age;
            this.userSex  = sex;

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.ani_slide_in, R.animator.ani_slide_out).replace(android.R.id.content, userInfoFragment, "USERINFO").addToBackStack("USERINFO").commit();
        }

    }

    public void goUserName(){
        this.imageByte = null;
        this.userEmail = "";
        this.userName = "";
        this.userFirstName = "";
        this.userGivenName = "";
        this.userAge = 0;
        this.userSex = "";

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void onLogin(int height, int weight) {
        if (getFragmentManager().findFragmentByTag("USERINFO") == userInfoFragment) {

            if (imageByte == null) {
                image = getResources().getDrawable(R.drawable.icon_nonprofile_m);

                if(userSex.equals("W"))
                    image = getResources().getDrawable(R.drawable.icon_nonprofile_w);

                BitmapDrawable bt = ((BitmapDrawable)image);
                Bitmap bitmap = bt.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageByte = stream.toByteArray();
            }

            this.userHeight = height;
            this.userWeight = weight;


            ArrayList<Object> arrayUpdates = new ArrayList<Object>();
            arrayUpdates.add(imageByte);
            arrayUpdates.add(userName);
            arrayUpdates.add(userFirstName);
            arrayUpdates.add(userGivenName);
            arrayUpdates.add(userEmail);
            arrayUpdates.add(userAge);
            arrayUpdates.add(userSex);
            arrayUpdates.add(userHeight);
            arrayUpdates.add(userWeight);

            Log.d("상은", "userSex : " + userSex);

            dbManager.onInsertUser(arrayUpdates);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getFragmentManager().findFragmentByTag("USERNAME");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
