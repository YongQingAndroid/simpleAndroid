package com.zyq.simplecitypicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CityPickerView extends LinearLayout {
    RadioGroup group;
    ListView listView;
    Adapter adapter;
    String[]lables=new String[]{"省","市","区"};
    int selectIdex = 0;
    RadioButton[] radioButtons = new RadioButton[3];
    CityDataSource mCityDataSource = new CityDataSource();
    CityDataArrayList listData = new CityDataArrayList();
    OnClickListener itemClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = ((RadioBean) v.getTag()).index;
            if(index==selectIdex){
                return;
            }else {
                selectIdex=index;
            }
            listData.clear();
            if (selectIdex!=0&&((RadioBean) radioButtons[selectIdex].getTag()).cityBean == null){
                adapter.notifyDataSetChanged();
                return;
            }
            goNext(-1);
        }
    };

    public CityPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    private void initUi() {
        setOrientation(VERTICAL);
        group = new RadioGroup(getContext());
        group.setOrientation(HORIZONTAL);
        RadioButton radioProvince = new RadioButton(getContext());
        radioProvince.setId(getId(10001));
        radioProvince.setTag(new RadioBean(0));
        radioProvince.setText("省");
        radioProvince.setOnClickListener(itemClick);
        radioProvince.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
//        radioProvince.setClickable(false);
        radioButtons[0] = radioProvince;


        group.addView(radioProvince);
        RadioButton radioCity = new RadioButton(getContext());
        radioCity.setId(getId(10002));
        radioCity.setText("市");
        radioCity.setTag(new RadioBean(1));
        radioCity.setOnClickListener(itemClick);
        radioCity.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
//        radioCity.setClickable(false);
        radioButtons[1] = radioCity;

        group.addView(radioCity);
        RadioButton radioArea = new RadioButton(getContext());
        radioArea.setId(getId(10003));
        radioArea.setText("区");
        radioArea.setTag(new RadioBean(2));
        radioArea.setOnClickListener(itemClick);
        radioArea.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
//        radioArea.setClickable(false);
        radioButtons[2] = radioArea;


        group.addView(radioArea);
        addView(group);
        listView = new ListView(getContext());
        listView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        adapter = new Adapter(listData);
        listView.setAdapter(adapter);
        addView(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goNext(position);
            }
        });

        praseData();
    }

    private void goNext(int position) {
        CityBean mCityBean = null;
        if (position == -1) {
            if (selectIdex > 0) {
                mCityBean = ((RadioBean) radioButtons[selectIdex - 1].getTag()).cityBean;
            }
        } else {
            mCityBean = listData.get(position);
            radioButtons[selectIdex].setText(mCityBean.getName());
            radioButtons[selectIdex].setChecked(true);
            ((RadioBean) radioButtons[selectIdex].getTag()).cityBean = mCityBean;
            if (selectIdex >= 2) {
                return;
            } else {
                selectIdex++;
            }
        }

        switch (selectIdex) {
            case 0:
                listData.clear();
                listData.addAll(mCityDataSource.getProvince());
                adapter.notifyDataSetChanged();
                break;
            case 1:
                listData.clear();
                listData.addAll(mCityDataSource.getCity(mCityBean.getId()));
                adapter.notifyDataSetChanged();
                break;
            case 2:
                listData.clear();
                listData.addAll(mCityDataSource.getArea(mCityBean.getId()));
                adapter.notifyDataSetChanged();
                break;
        }
        resitData();

    }

    private void resitData() {
        int arg = selectIdex;
        while (arg < 3) {
            radioButtons[arg].setText("");
            ((RadioBean) radioButtons[arg].getTag()).cityBean = null;
            radioButtons[arg].setText(lables[arg]);
//            radioButtons[arg].setClickable(false);
            arg++;
        }
    }

    private int getId(int arg) {
        return arg;
    }

    private void praseData() {
        listData.clear();
        listData.addAll(mCityDataSource.getProvince());
        adapter.notifyDataSetChanged();
    }

    private static class Adapter extends BaseAdapter {
        CityDataArrayList list;

        Adapter(CityDataArrayList list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AdapterHolder adapterHolder;
            if (convertView == null) {
                adapterHolder = AdapterHolder.getHolder(parent.getContext());
                convertView = adapterHolder.rootView;
            } else {
                adapterHolder = (AdapterHolder) convertView.getTag();
            }
            adapterHolder.rootView.setText(list.get(position).getName());
            return convertView;
        }
    }

    private static class AdapterHolder {
        TextView rootView;

        AdapterHolder(TextView rootView) {
            this.rootView = rootView;
            rootView.setTag(this);
        }

        public static AdapterHolder getHolder(Context context) {
            TextView itemView = new TextView(context);

            itemView.setPadding(0,HookApplication.px2dip(itemView.getContext(),10),0,HookApplication.px2dip(itemView.getContext(),10));
            return new AdapterHolder(itemView);
        }
    }

    private static class RadioBean {
        public int index;
        CityBean cityBean;
        RadioBean(int index) {
            this.index = index;
        }
    }
}
