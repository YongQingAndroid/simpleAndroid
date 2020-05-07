package com.zyq.jsimleplepicker;

import android.view.View;
import android.widget.TextView;

import com.zyq.jsimleplepicker.timePicker.FormatState;

import java.util.List;

/**
 * package kotlinTest:com.qing.lightview.common.PickerViewInterface.class
 * 作者：zyq on 2017/8/8 16:09
 * 邮箱：zyq@posun.com
 */
public interface TimePickerViewInterface<T> extends PickerViewInterface<T>{

     void setCustomTimePicker(String lable, FormatState state, List<String> data, int selectedPosition);
     void bindTextView(TextView textView);
     PickerViewInterface simPleShow();
}
