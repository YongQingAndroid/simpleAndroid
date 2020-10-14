package com.zyq.simpleandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.jfz.wealth.R;

public class Main2Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void goLogin(View view) {
        AutoLoginManager.getInstance().goLogin(this);
    }

    public void getToken(View view) {
        AutoLoginManager.getInstance().getToken(this);
    }
}
