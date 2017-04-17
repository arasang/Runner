package park.sangeun.runner.Common;

import android.Manifest;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by user on 2017-03-31.
 */

public class Metrics {
    public static final int DATABASE_VERSION = 2;

    public static final String[] PERMISSION_LIST = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    public static final String DATABASE_NAME = "RUNNER.db";
    public static final String DATABASE_RECORD_TABLE_NAME = "RECORD_TABLE";
    public static final String DATABASE_RECORD_DETAIL_TABLE_NAME = "RECORD_DETAIL";
    public static final String DATABASE_USER_TABLE_NAME = "USER_INFO";

    public static final String ACTIVITY_RUN = "RUN";
    public static final String ACTIVITY_WALK = "WALK";
    public static final String ACTIVITY_BICYCLE = "BICYCLE";

    public static final String CLOSE_INTENT = "close";

    public static final int ACTIVITY_SELECT = 10;

    public static final int GALLERY_PICTURE = 100;
    public static final int CAMERA_REQUEST = 101;

    public static final int PERMISSION_LOCATION = 1;
    public static final int DB_UPDATE_SUCCESS = 2;
    public static final int DB_UPDATE_FAILED = 3;
    public static final int DB_SELECT_SUCCESS = 4;
    public static final int DB_SELECT_FAILED  = 5;
    public static final int DB_INSERT_SUCCESS = 6;
    public static final int DB_INSERT_FAILED  = 7;
    public static final int DB_DELETE_SUCCESS = 8;
    public static final int DB_DELETE_FAILED  = 9;

    public static final int MULTIPLE_PERMISSIONS = 1000;
    public static final int RC_SIGN_IN = 2000;

    public static GoogleSignInAccount acct = null;
    public static GoogleApiClient mGoogleApiClient;
}
