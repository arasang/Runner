package park.sangeun.runner.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import java.util.HashMap;

import park.sangeun.runner.Common.DBManager;
import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-06.
 */

/**
    회원 정보가 있을 경우 Main으로 이동
    아닐 경우 회원 정보 입력 화면 노출
 **/
public class SplashActivity extends Activity implements View.OnClickListener{

    //UI
    private RelativeLayout buttonGoogle;
    private Button buttonLogin;

    // Properties
    public static Activity splashActivity;
    private DBManager dbManager = new DBManager(this, Metrics.DATABASE_NAME, null, Metrics.DATABASE_VERSION);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashActivity = this;
        checkLoginUser();
        setContentView(R.layout.activity_splash);

        initView();

    }

    private void initView(){
        buttonGoogle = (RelativeLayout) findViewById(R.id.buttonGoogle);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);

        buttonGoogle.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
    }

    private void checkLoginUser(){
        dbManager = new DBManager(this, Metrics.DATABASE_NAME, null, Metrics.DATABASE_VERSION);
        try {
            String[] paramsSelect = new String[]{"USER_IMAGE", "USER_NAME", "USER_FIRST_NAME", "USER_GIVEN_NAME", "USER_EMAIL", "USER_AGE", "USER_SEX", "USER_HEIGHT", "USER_WEIGHT", "ANNUAL_GOAL"};
            HashMap<String,String> paramsWhere = new HashMap<String, String>();

            paramsWhere.put("_id", "1");

            HashMap<String,Object> result = dbManager.onSelectUser(Metrics.DATABASE_USER_TABLE_NAME, paramsSelect, paramsWhere);

            if (!result.get("USER_NAME").equals("")) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonGoogle:
                Intent googleIntent = new Intent(SplashActivity.this, GoogleLoginActivity.class);
                startActivity(googleIntent);
                break;

            case R.id.buttonLogin:
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: 사용자 정보 검색
    }
}
