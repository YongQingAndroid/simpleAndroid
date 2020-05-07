package com.zyq.jsimleplepicker.timePicker;

import android.content.Context;
import android.view.View;

import com.zyq.jsimleplepicker.PickerViewInterface;
import com.zyq.jsimleplepicker.TimePickerViewInterface;
import com.zyq.jsimleplepicker.dialog.LightDialog;

import java.util.Date;

public class TimePickerManager {
    static TimePickerManager self;
    public synchronized static TimePickerManager getInstance() {
        if(self==null){
            self=new TimePickerManager();
        }
        return self;
    }
    public TimePickerViewInterface showPicker(Context context, Date date, FormatState... formatstates){
        TimePickerViewInterface viewInterface=new MaterialTimePickerLayout(context,date,formatstates);
        viewInterface.simPleShow();
        return viewInterface;
    }

    public PickerViewInterface showPicker(Context context,FormatState... formatstates){
        return showPicker(context, new Date(),formatstates);
    }
}
