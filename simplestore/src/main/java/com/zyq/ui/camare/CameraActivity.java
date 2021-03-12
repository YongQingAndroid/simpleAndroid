package com.zyq.ui.camare;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.zyq.permission.OnPermission;
import com.zyq.permission.Permission;
import com.zyq.permission.QPermissions;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author 郭翰林
 * @date 2019/2/28 0028 16:23
 * 注释:Android自定义相机
 */
public class CameraActivity extends AppCompatActivity {
    CameraView cameraView;
    private static CameraView.CameraCall mCameraCall;

    public static void startCamera(Context activity, CameraView.CameraCall cameraCall) {
        mCameraCall = cameraCall;
        activity.startActivity(new Intent(activity, CameraActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置为无标题(去掉Android自带的标题栏)，(全屏功能与此无关)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initUI();
    }

    @Override
    protected void onDestroy() {
        mCameraCall = null;
        super.onDestroy();
    }

    public CameraView.CameraCall getCall() {
        return mCameraCall;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initUI() {
        cameraView = new CameraView(this);
        setContentView(cameraView);
        cameraView.setCameraCall((flag, path) -> {

            CameraView.CameraCall cameraCall = getCall();
            if (cameraCall != null) {
                cameraCall.call(flag, path);
            }
            finish();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume() {
        super.onResume();
        QPermissions.with(this).permission(Permission.CAMERA,Permission.MANAGE_EXTERNAL_STORAGE).request(new OnPermission() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                if (all) {
                    cameraView.startCamera();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean never) {

            }
        });
    }
}
