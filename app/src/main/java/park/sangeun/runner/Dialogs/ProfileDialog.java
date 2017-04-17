package park.sangeun.runner.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.R;

/**
 * Created by user on 2017-04-10.
 */

public class ProfileDialog {
    private Activity context;

    public ProfileDialog(Activity context){
        this.context = context;

    }

    public void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(context);
        myAlertDialog.setTitle(context.getString(R.string.ChangeProfileTitle));
        myAlertDialog.setMessage(context.getString(R.string.ChangeProfileMessage));

        myAlertDialog.setPositiveButton(context.getString(R.string.MethodGallery),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;
                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        context.startActivityForResult(
                                pictureActionIntent,
                                Metrics.GALLERY_PICTURE);
                    }
                }
        );

        myAlertDialog.setNegativeButton(context.getString(R.string.MethodCamera),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");

                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(context, "park.sangeun.runner.provider", f));

                        context.startActivityForResult(intent,
                                Metrics.CAMERA_REQUEST);
                    }
                }
        );
        myAlertDialog.show();
    }
}
