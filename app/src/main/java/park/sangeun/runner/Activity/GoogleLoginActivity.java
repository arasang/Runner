package park.sangeun.runner.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import park.sangeun.runner.Common.DBManager;
import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-12.
 */

public class GoogleLoginActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {
    private LinearLayout layoutRoot;

    private DBManager dbManager = new DBManager(this, Metrics.DATABASE_NAME, null, Metrics.DATABASE_VERSION);

    private GoogleSignInOptions gso
            = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutRoot = new LinearLayout(this);
        layoutRoot.setOrientation(LinearLayout.VERTICAL);
        setContentView(layoutRoot);


        Metrics.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signIn();

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(Metrics.mGoogleApiClient);
        startActivityForResult(signInIntent, Metrics.RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Metrics.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if (result.isSuccess()) {
            Metrics.acct = result.getSignInAccount();

            Drawable image = getResources().getDrawable(R.drawable.icon_nonprofile_m);

            BitmapDrawable bt = ((BitmapDrawable)image);
            Bitmap bitmap = bt.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageByte = stream.toByteArray();

            ArrayList<Object> userInfo = new ArrayList<Object>();

            userInfo.add(imageByte);
            userInfo.add(Metrics.acct.getDisplayName());
            userInfo.add(Metrics.acct.getFamilyName());
            userInfo.add(Metrics.acct.getGivenName());
            userInfo.add(Metrics.acct.getEmail());
            userInfo.add((long)20);
            userInfo.add("M");
            userInfo.add((long)165);
            userInfo.add((long)60);
            dbManager.onInsertUser(userInfo);

            Intent intent = new Intent(GoogleLoginActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
            if (SplashActivity.splashActivity != null) {
                SplashActivity.splashActivity.finish();
            }

        } else {
            AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setTitle(getResources().getString(R.string.ErrorTitle));
            alert.setMessage(getResources().getString(R.string.GoogleLoginErrorMessage));
            alert.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alert.show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
