package com.zyq.jsimleplepicker.citypicker;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;

import android.view.View;
import android.widget.CheckBox;

import com.zyq.jsimleplepicker.Utils;
import com.zyq.jsimleplepicker.dialog.LightDialog;
import com.zyq.jsimleplepicker.textView.LightRichBubbleText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MeterialCityDialog extends LightDialog {
//    private Context context;
    private HapticFeedbackController mHapticFeedbackController;
    private LightRichBubbleText province_text, city_tx, area_tx, street_tx;
    private RecyclerView mRecyclerView;
    private CityDataArrayList provinces;
    private CityDataArrayList areas;
    private CityDataArrayList citys;
    private CityDataArrayList streets;
    private CityAdapter adapter;
    private State state = State.PROVINCE;
    private CityView rootview;
    private int mThemeColor = -1;
    private CityDataArrayList adapterData = new CityDataArrayList();
    private CityDataSource dataSource = new CityDataSource();


    public MeterialCityDialog(Context context) {
        super(context);
        setGravity(LightDialog.QGriavty.CENTER);
        setHasPadding(true);
        mHapticFeedbackController = new HapticFeedbackController(context);
        PraseTheme();
        setContentView(getView());
        initdata();
        praseUI();

    }

    private void PraseTheme() {
        if (mThemeColor == -1) {
            mThemeColor = Utils.getAccentColorFromThemeIfAvailable(getContext());
        }
    }


    private void initdata() {
        provinces = dataSource.getProvince();
        if (provinces == null || provinces.size() < 1)
            return;
        province_text.setText(provinces.get(0).getName());
        province_text.setTag(0);
        citys = dataSource.getCity(provinces.get(0).getId());
        if (citys != null && citys.size() > 0) {
            city_tx.setText(citys.get(0).getName());
            city_tx.setTag(0);
            areas = dataSource.getArea(citys.get(0).getId());
        } else {
            areas = new CityDataArrayList();
        }
        if (areas != null && areas.size() > 0) {
            area_tx.setText(areas.get(0).getName());
            area_tx.setTag(0);
            streets = dataSource.getStreet(areas.get(0).getId());
        } else {
            streets = new CityDataArrayList();
        }
        if (streets.size() > 0) {
            street_tx.setText(streets.get(0).getName());
            street_tx.setTag(0);
        }
    }

    public void dataChange(CityDataArrayList list) {
        adapterData.clear();
        if (list != null)
            adapterData.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void show() {
        super.show();
        province_text.setChecked(true);
        PulseAnimator(province_text);
    }

    private void PulseAnimator(View view) {
        ObjectAnimator pulseAnimator = Utils.getPulseAnimator(view, 0.85f, 1.1f);
        pulseAnimator.setStartDelay(300);
        pulseAnimator.start();
        mHapticFeedbackController.tryVibrate();
    }

//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View getView() {
        rootview = new CityView(getContext(),mThemeColor);
        province_text = rootview.getProvince_text();
        city_tx = rootview.getCity_tx();
        area_tx = rootview.getArea_tx();
        street_tx = rootview.getStreet_tx();
        mRecyclerView = rootview.getRecyclerView();
        return rootview;
    }

    private void praseUI() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterData.addAll(provinces);
        adapter = new CityAdapter(adapterData, mThemeColor);
        mRecyclerView.setAdapter(adapter);
        rootview.getMdtp_cancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        province_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = State.PROVINCE;
                praseChick((CheckBox) view);
                adapter.setSelectPosition((Integer) view.getTag());
                dataChange(provinces);
            }
        });
        city_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = State.CITY;
                praseChick((CheckBox) view);
                adapter.setSelectPosition((Integer) view.getTag());
                dataChange(citys);
            }
        });
        area_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = State.AREA;
                praseChick((CheckBox) view);
                adapter.setSelectPosition((Integer) view.getTag());
                dataChange(areas);
            }
        });
        street_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = State.STREET;
                praseChick((CheckBox) view);
                adapter.setSelectPosition((Integer) view.getTag());
                dataChange(streets);
            }
        });
        adapter.setListener(new CityAdapter.Listener() {
            @Override
            public void onItemClick(int position, View view) {
                praseItemChick(state, position);
            }
        });
    }

    private void praseChick(CheckBox checkBox) {
        province_text.setChecked(false);
        city_tx.setChecked(false);
        area_tx.setChecked(false);
        street_tx.setChecked(false);

        checkBox.setChecked(true);
        PulseAnimator(checkBox);
    }


    private void praseItemChick(State state, int position) {
        switch (state) {
            case PROVINCE:
                province_text.setText(provinces.get(position).getName());
                province_text.setTag(position);
                citys = dataSource.getCity(provinces.get(position).getId());
                this.state = State.CITY;
                dataChange(citys);
                adapter.setSelectPosition(0);
                praseChick(city_tx);
                if (citys.size() > 0) {
                    city_tx.setText(citys.get(0).getName());
                    city_tx.setTag(0);
                    areas = dataSource.getArea(citys.get(0).getId());
                }else{
                    city_tx.setText("");
                    city_tx.setTag(0);
                }
                if (areas.size() > 0) {
                    area_tx.setText(areas.get(0).getName());
                    area_tx.setTag(0);
                    streets = dataSource.getStreet(areas.get(0).getId());
                }else{
                    area_tx.setText("");
                    area_tx.setTag(0);
                }
                if(streets.size()>0){
                    street_tx.setText(streets.get(0).getName());
                    street_tx.setTag(0);
                }else{
                    street_tx.setText("");
                    street_tx.setTag(0);
                }
                break;
            case CITY:
                city_tx.setText(citys.get(position).getName());
                city_tx.setTag(position);
                areas = dataSource.getArea(citys.get(position).getId());
                dataChange(areas);
                adapter.setSelectPosition(0);
                praseChick(area_tx);
                this.state = State.AREA;
                if (areas.size() > 0) {
                    area_tx.setText(areas.get(0).getName());
                    area_tx.setTag(0);
                    streets = dataSource.getStreet(areas.get(0).getId());
                }else{
                    area_tx.setText("");
                    area_tx.setTag(0);
                }
                if(streets.size()>0){
                    street_tx.setText(streets.get(0).getName());
                    street_tx.setTag(0);
                }else{
                    street_tx.setText("");
                    street_tx.setTag(0);
                }
                break;
            case AREA:
                area_tx.setText(areas.get(position).getName());
                area_tx.setTag(position);
                streets = dataSource.getStreet(areas.get(position).getId());
                dataChange(streets);
                adapter.setSelectPosition(0);
                praseChick(street_tx);
                this.state = State.STREET;

                if(streets.size()>0){
                    street_tx.setText(streets.get(0).getName());
                    street_tx.setTag(0);
                }else{
                    street_tx.setText("");
                    street_tx.setTag(0);
                }
                break;
            case STREET:
                street_tx.setText(streets.get(position).getName());
                street_tx.setTag(position);
                adapter.setSelectPosition(position);
                adapter.notifyDataSetChanged();
                PulseAnimator(street_tx);
                break;
        }
    }

    public enum State {
        PROVINCE,
        CITY,
        AREA,
        STREET;

    }
}
