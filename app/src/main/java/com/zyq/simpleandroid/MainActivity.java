package com.zyq.simpleandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.zyq.jsimleplepicker.citypicker.MeterialCityDialog;
import com.zyq.jsimleplepicker.timePicker.FormatState;
import com.zyq.jsimleplepicker.timePicker.TimePickerManager;
import com.zyq.simplecitypicker.SqliteDataSource;
import com.zyq.simplecitypicker.SqliteDataSource1;
import com.zyq.simplestore.SimpleStore;
import com.zyq.simplestore.log.LightLog;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void mClick(View view){
        SimpleStore.store("name","5656565656");
        SimpleStore.openMmap();
        LightLog.I(SimpleStore.praseKey("name").get(String.class));

    }
    public void mClicktime(View view){
//        TimePickerManager.getInstance().showPicker(this, FormatState.YYYY, FormatState.MM,FormatState.DD,FormatState.HH,FormatState.mm.setJump(30));
       new SqliteDataSource1().execute();

    }
    public void mClickcity(View view){

        new MeterialCityDialog(this).show();
    }

}
