package com.zyq.simpleandroid;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jfz.wealth.R;

import com.zyq.permission.OnPermission;
import com.zyq.permission.Permission;
import com.zyq.permission.QPermissions;

import com.zyq.ui.camare.CameraView;

import java.util.List;

import androidx.annotation.RequiresApi;


public class Main2Activity extends BaseActivity {
    CameraView cameraView = null;
    LinearLayout group;
    ImageView img1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Gson.setStrictMode(false);
        img1 = findViewById(R.id.img1);
        cameraView = findViewById(R.id.camera);
        cameraView.setCameraCall((flag, path) -> {
            if (flag) {
                Glide.with(Main2Activity.this).load(path).into(img1);
            }
        });
    }


    public void goLogin(View view) {
////        AutoLoginManager.getInstance().goLogin(this);
//        CameraActivity.startMe(this, 2005, CameraActivity.MongolianLayerType.IDCARD_POSITIVE);
        QPermissions.with(this).permission(Permission.REQUEST_INSTALL_PACKAGES, Permission.CAMERA, Permission.CALL_PHONE).request(new OnPermission() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                if (all) {
                    Toast.makeText(Main2Activity.this, "授权成功", Toast.LENGTH_SHORT).show();
                    cameraView.startCamera();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean never) {

            }
        });
//        QPhotoUtils.authorities = "com.jfz.wealth.fileprovider";
//        QPhotoUtils.camera(this, (uri, result, arg) -> {
//            QCompression.newInstance()
//                    .getCompressionBuilder(Main2Activity.this)
//                    .from(uri)
//                    .setMaxSize(100)
//                    .get(new QCompression.CompressionCallback() {
//                        @Override
//                        public void onStart(Context context) {
//
//                        }
//
//                        @Override
//                        public void onSuccess(List<File> files) {
//                            File file = files.get(0);
//                            ImageView img1 = findViewById(R.id.img1);
//                            Glide.with(Main2Activity.this).load(file).into(img1);
//                            Toast.makeText(Main2Activity.this, "" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onErr(Exception e) {
//
//                        }
//                    });
//
//        });
    }

    public void getToken(View view) {
        AutoLoginManager.getInstance().getToken(this);
    }
}
