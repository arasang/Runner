package park.sangeun.runner.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.support.design.widget.TextInputEditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import park.sangeun.runner.Activity.LoginActivity;
import park.sangeun.runner.Common.Animation.ButtonAnimation;
import park.sangeun.runner.Common.Metrics;
import park.sangeun.runner.Common.RoundingDrawable;
import park.sangeun.runner.Dialogs.ProfileDialog;
import park.sangeun.runner.R;

import static android.app.Activity.RESULT_OK;


/**
 * Created by user on 2017-04-06.
 */

public class UserNameFragment extends Fragment implements View.OnClickListener{

    private ImageView imageProfile;
    private TextInputEditText editEmail;
    private TextInputEditText editFirstName;
    private TextInputEditText editGivenName;
    private TextInputEditText editAge;
    private Button buttonMan;
    private Button buttonGirl;

    private String email = "";
    private String sex="M";
    private String name = "";
    private String firstName = "";
    private String givenName = "";
    private int age = 0;

    private Bitmap bitmap;
    private String selectedImagePath;
    private byte[] imageByte;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_login, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_username, container, false);

        imageProfile = (ImageView) v.findViewById(R.id.imageProfile);
        editEmail = (TextInputEditText) v.findViewById(R.id.editEmail);
        editFirstName = (TextInputEditText) v.findViewById(R.id.editFirstName);
        editGivenName = (TextInputEditText) v.findViewById(R.id.editGivenName);
        editAge = (TextInputEditText) v.findViewById(R.id.editAge);
        buttonMan = (Button) v.findViewById(R.id.buttonMan);
        buttonGirl = (Button) v.findViewById(R.id.buttonGirl);

        imageProfile.setOnClickListener(this);
        buttonMan.setOnClickListener(this);
        buttonGirl.setOnClickListener(this);

        editEmail.addTextChangedListener(textWatcher);
        editFirstName.addTextChangedListener(textWatcher);
        editGivenName.addTextChangedListener(textWatcher);
        editAge.addTextChangedListener(textWatcher);


        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onClick(View view) {
        if (view == imageProfile) {
            if (checkPermissions()) {
                ProfileDialog dialog = new ProfileDialog(getActivity());
                dialog.startDialog();
            }
        }

        if (view == buttonMan) {
            sex = "M";
            buttonMan.setBackgroundResource(R.drawable.background_button_man);
            buttonGirl.setBackgroundResource(R.drawable.background_button_normal);
        }


        if (view == buttonGirl) {
            sex = "W";
            buttonMan.setBackgroundResource(R.drawable.background_button_normal);
            buttonGirl.setBackgroundResource(R.drawable.background_button_woman);
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<String>();
        for (String permission: Metrics.PERMISSION_LIST){
            result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(
                    getActivity(), permissionList.toArray(
                            new String[permissionList.size()]
                    ),
                    Metrics.MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                Toast.makeText(getActivity(), getResources().getString(R.string.CaptureError), Toast.LENGTH_SHORT).show();
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

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageByte = stream.toByteArray();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == Metrics.GALLERY_PICTURE) {
            if (data != null) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,
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


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageByte = stream.toByteArray();

            } else {
                Toast.makeText(getActivity(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        Toast.makeText(getActivity(), "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionNext) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            if (editEmail.getText().length() == 0) {
                Toast.makeText(getActivity(), "이메일 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                editEmail.startAnimation(animation);
                editEmail.requestFocus();
                editEmail.setTextColor(Color.RED);
            } else if(editFirstName.getText().length() == 0) {
                Toast.makeText(getActivity(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                editFirstName.startAnimation(animation);
                editFirstName.requestFocus();
                editFirstName.setTextColor(Color.RED);
            } else if(editGivenName.getText().length() == 0) {
                Toast.makeText(getActivity(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                editGivenName.startAnimation(animation);
                editGivenName.requestFocus();
                editGivenName.setTextColor(Color.RED);
            } else if(editAge.getText().length() == 0) {
                Toast.makeText(getActivity(), "나이를 입력해주세요", Toast.LENGTH_SHORT).show();
                editAge.startAnimation(animation);
                editAge.requestFocus();
                editAge.setTextColor(Color.RED);
            } else if (!editEmail.getText().toString().contains("@")) {
                Toast.makeText(getActivity(), "올바른 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                editEmail.startAnimation(animation);
                editEmail.requestFocus();
                editEmail.setTextColor(Color.RED);
            } else {
                Bitmap bitmap=null;
                if (imageByte != null) {
                    RoundingDrawable drawable = (RoundingDrawable) imageProfile.getDrawable();
                    bitmap = drawable.getBitmap();

                } else {
                    BitmapDrawable drawable = (BitmapDrawable)imageProfile.getDrawable();
                    bitmap = drawable.getBitmap();
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                imageByte = stream.toByteArray();
                email = editEmail.getText().toString().replace(" ", "");
                firstName = editFirstName.getText().toString().replace(" ", "");
                givenName = editGivenName.getText().toString().replace(" ", "");
                name = (editFirstName.getText().toString()).replace(" ", "") + editGivenName.getText().toString().replaceAll(" ", "");
                age = Integer.parseInt((editAge.getText().toString()).replace(" ", ""));
                ((LoginActivity) getActivity()).goUserInfo(imageByte, email, name, firstName, givenName, age, sex);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            editEmail.setTextColor(Color.BLACK);
            editFirstName.setTextColor(Color.BLACK);
            editGivenName.setTextColor(Color.BLACK);
            editAge.setTextColor(Color.BLACK);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
