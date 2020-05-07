package com.zyq.jsimleplepicker;

import android.app.Activity;

import com.zyq.jsimleplepicker.citypicker.MeterialCityDialog;
import com.zyq.jsimleplepicker.dialog.LightDialog;
import com.zyq.jsimleplepicker.timePicker.MaterialTimePickerLayout;
import com.zyq.jsimleplepicker.timePicker.TimePickerManager;

public class Demo extends Activity {
    public  void main(String[] args) {
        TimePickerManager.getInstance().showPicker(this);
        new MeterialCityDialog(this).show();
    }
}
