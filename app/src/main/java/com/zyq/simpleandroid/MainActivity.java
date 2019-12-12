package com.zyq.simpleandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.zyq.simplestore.SimpleStore;
import com.zyq.simplestore.log.LightLog;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void mClick(View view){
//        SimpleStore.store("name","5656565656");
//        LightLog.I(SimpleStore.praseKey("name").get(String.class));
        SimpleStore.openMMAP();
    }

}
