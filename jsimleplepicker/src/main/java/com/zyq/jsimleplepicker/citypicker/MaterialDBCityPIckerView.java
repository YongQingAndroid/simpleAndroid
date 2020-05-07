package com.zyq.jsimleplepicker.citypicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyq.jsimleplepicker.PickerViewInterface;
import com.zyq.jsimleplepicker.QlightUnit;
import com.zyq.jsimleplepicker.Utils;
import com.zyq.jsimleplepicker.WheelPicker;
import com.zyq.jsimleplepicker.textView.LightRichBubbleText;
import com.zyq.jsimleplepicker.timePicker.FormatState;

import java.util.List;

/**
 * package OKSALES_PDA:com.resources.MaterialDBCityPIckerView.class
 * 作者：zyq on 2017/3/7 12:41
 * 邮箱：zyq@posun.com
 */
public class MaterialDBCityPIckerView extends LinearLayout implements PickerViewInterface {
    private LinearLayout IOSContent, titleView;
    private LayoutParams pickerLp;
    public LightRichBubbleText sure, cancel;
    private int color = -1;
    private WheelPicker[] pickers = new WheelPicker[4];
    private State[] states;
    private int city_point = -1;
    private int area_point = -1;
    private int street_point = -1;
    private int item_text_size = 17;
    private CityDataArrayList provinces;
    private CityDataArrayList areas;
    private CityDataArrayList citys;
    private CityDataArrayList streets;
    private CityDataSource dataSource = new CityDataSource();

    public MaterialDBCityPIckerView(Context context, State... args) {
        super(context);
        this.states = args;
        if (color == -1) {
            color = Utils.getAccentColorFromThemeIfAvailable(context);
        }
        init();
    }

    public MaterialDBCityPIckerView(Context context, int color, State... args) {
        super(context);
        this.states = args;
        this.color = color;
        if (color == -1) {
            color = Utils.getAccentColorFromThemeIfAvailable(context);
        }
        init();
    }

    public void setThemeColor(int color) {
        this.color = color;
    }



    @Override
    public View getView() {
        return this;
    }

    @Override
    public View getOkView() {
        return sure;
    }

    @Override
    public View getCancelView() {
        return cancel;
    }

    @Override
    public Object getValue() {
        return null;
    }

    public String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (WheelPicker picker : pickers) {
            if (picker != null){
                stringBuilder.append(picker.getCurrentItemString());
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString().substring(0,stringBuilder.length()-1);
    }

    private void praseData() {
        provinces = dataSource.getProvince();
        if (provinces == null || provinces.size() < 1)
            return;
        citys = dataSource.getCity(provinces.get(0).getId());
        if (citys != null && citys.size() > 0) {
            areas = dataSource.getArea(citys.get(0).getId());
        } else {
            areas = new CityDataArrayList();
        }
        if (areas != null && areas.size() > 0) {
            streets = dataSource.getStreet(areas.get(0).getId());
        } else {
            streets = new CityDataArrayList();
        }
        addPickerUi();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init() {
        this.setOrientation(LinearLayout.VERTICAL);
        int padding = QlightUnit.dip2px(getContext(), 10);
        int contentHeight = QlightUnit.dip2px(getContext(), 220);
        titleView = new LinearLayout(getContext());
        titleView.setOrientation(LinearLayout.HORIZONTAL);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        initTitleView();
        this.addView(titleView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        IOSContent = new LinearLayout(getContext());
        IOSContent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, contentHeight));
        IOSContent.setBackgroundColor(Color.WHITE);
        IOSContent.setPadding(0, padding, 0, padding);
        this.addView(IOSContent);
        pickerLp = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        praseData();
    }

    private void initTitleView() {
        int left = QlightUnit.dip2px(getContext(), 25);
        int top = QlightUnit.dip2px(getContext(), 10);
        titleView.setPadding(left, top, left, top);
        sure = new LightRichBubbleText(getContext());
        sure.setText("确定");
        sure.setText_press_color(0X4CFFFFFF);
        sure.setText_color(Color.WHITE);
        sure.setText_active_color(Color.WHITE);
        sure.commit();
        cancel = new LightRichBubbleText(getContext());
        cancel.setText("取消");
        cancel.setText_press_color(0X4CFFFFFF);
        cancel.setText_color(Color.WHITE);
        cancel.setText_active_color(Color.WHITE);
        cancel.commit();
        TextView title = new TextView(getContext());
        title.setText("选择地址");
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        titleView.addView(cancel);
        titleView.addView(title, lp);
        titleView.addView(sure);
        titleView.setBackgroundColor(color);
    }

    private void setListener() {
        for (int i = 0; i < states.length; i++) {
            final int point = i;
            pickers[i].setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker picker, Object data, int position) {
                    UpData(states[point], position, String.valueOf(data));
                }
            });
        }

    }

    private void UpData(State state, int position, String value) {
        switch (state) {
            case PROVINCE:
                if (city_point != -1 && pickers[city_point] != null) {
                    citys = dataSource.getCity(provinces.get(position).getId());
                    pickers[city_point].setData(citys.toListData());
                    pickers[city_point].setSelectedItemPosition(0);
                }
                if (area_point != -1 && pickers[area_point] != null && citys.size() > 0) {
                    areas = dataSource.getArea(citys.get(0).getId());
                    pickers[area_point].setData(areas.toListData());
                    pickers[area_point].setSelectedItemPosition(0);
                }
                if (street_point != -1 && pickers[street_point] != null && areas.size() > 0) {
                    streets = dataSource.getStreet(areas.get(0).getId());
                    pickers[street_point].setData(streets.toListData());
                    pickers[street_point].setSelectedItemPosition(0);
                }
                break;
            case CITY:
                if (area_point != -1 && pickers[area_point] != null) {
                    areas = dataSource.getArea(citys.get(position).getId());
                    pickers[area_point].setData(areas.toListData());
                    pickers[area_point].setSelectedItemPosition(0);
                }
                if (street_point != -1 && pickers[street_point] != null && areas.size() > 0) {
                    streets = dataSource.getStreet(areas.get(0).getId());
                    pickers[street_point].setData(streets.toListData());
                    pickers[street_point].setSelectedItemPosition(0);
                }
                break;
            case AREA:
                if (street_point != -1 && pickers[street_point] != null) {
                    streets = dataSource.getStreet(areas.get(position).getId());
                    pickers[street_point].setData(streets.toListData());
                    pickers[street_point].setSelectedItemPosition(0);
                }
                break;
        }
    }

    private void addPickerUi() {
        if (QlightUnit.isEmpty(states)) {
            states=new State[]{State.PROVINCE,State.CITY,State.AREA,State.STREET};
        }
        for (int i = 0; i < states.length; i++) {
            states[i].point = i;
            addPickerView(states[i]);
        }
        setListener();
    }

    private void addPickerView(State item) {
        switch (item) {
            case PROVINCE:
                WheelPicker p_picler = new WheelPicker(getContext(), color, item_text_size);
                p_picler.setLayoutParams(pickerLp);
//                p_picler.setCyclic(false);
                pickers[item.point] = p_picler;
                p_picler.setData(provinces.toListData());
                p_picler.setSelectedItemPosition(0);
                IOSContent.addView(p_picler);
                break;
            case CITY:
                WheelPicker c_picler = new WheelPicker(getContext(), color, item_text_size);
                c_picler.setLayoutParams(pickerLp);
//                c_picler.setCyclic(false);
                c_picler.setData(citys.toListData());
                c_picler.setSelectedItemPosition(0);
                city_point = item.point;
                pickers[item.point] = c_picler;
                IOSContent.addView(c_picler);
                break;
            case AREA:
                WheelPicker a_picler = new WheelPicker(getContext(), color, item_text_size);
                a_picler.setLayoutParams(pickerLp);
//                a_picler.setCyclic(false);
                a_picler.setData(areas.toListData());
                a_picler.setSelectedItemPosition(0);
                area_point = item.point;
                pickers[item.point] = a_picler;
                IOSContent.addView(a_picler);
                break;
            case STREET:
                WheelPicker s_picler = new WheelPicker(getContext(), color, item_text_size);
                s_picler.setLayoutParams(pickerLp);
//                s_picler.setCyclic(false);
                s_picler.setData(streets.toListData());
                s_picler.setSelectedItemPosition(0);
                street_point = item.point;
                pickers[item.point] = s_picler;
                IOSContent.addView(s_picler);
                break;
        }
    }

    public enum State {
        PROVINCE(0),
        CITY(0),
        AREA(0),
        STREET(0);
        int point;

        State(int point) {
            this.point = point;
        }
    }
}
