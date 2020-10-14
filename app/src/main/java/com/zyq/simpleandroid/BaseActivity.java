package com.zyq.simpleandroid;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static final String[] requestPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE

    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndRequestPermission();
    }

    private void checkAndRequestPermission() {
        SimplePermission mSimplePermission = new SimplePermission();
        mSimplePermission.requestPermissions(this, result -> {
            if (result.state == State.DONE) {

            } else {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT);

            }
        }, requestPermissions);
    }
}
