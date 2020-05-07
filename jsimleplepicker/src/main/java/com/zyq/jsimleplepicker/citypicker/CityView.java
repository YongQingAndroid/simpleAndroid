package com.zyq.jsimleplepicker.citypicker;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyq.jsimleplepicker.QlightUnit;
import com.zyq.jsimleplepicker.Utils;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;




public class CityView extends LinearLayout {
    private RecyclerView recyclerView;
    private CityTextView mdtp_cancel, mdtp_sure;
    private CityTextView province_text, city_tx, area_tx, street_tx;
    private int TextColor = Color.parseColor("#76ffffff"), bgColor = Color.RED;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public CityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    public TextView getMdtp_cancel() {
        return mdtp_cancel;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public CityTextView getProvince_text() {
        return province_text;
    }

    public CityTextView getCity_tx() {
        return city_tx;
    }

    public CityTextView getArea_tx() {
        return area_tx;
    }

    public CityTextView getStreet_tx() {
        return street_tx;
    }

//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initUi() {
        setBackgroundColor(Color.WHITE);
        setOrientation(VERTICAL);
        TextView titleView = new TextView(getContext());
        titleView.setText("地址选择器");
        titleView.setTextColor(Color.WHITE);
        titleView.setGravity(Gravity.CENTER);
        titleView.setBackgroundColor(Utils.darkenColor(bgColor));
        addView(titleView, new LayoutParams(QlightUnit.dip2px(getContext(), 270), QlightUnit.dip2px(getContext(), 30)));
        LinearLayout contentTitle = new LinearLayout(getContext());
        contentTitle.setOrientation(VERTICAL);
        contentTitle.setBackgroundColor(bgColor);
        contentTitle.setPadding(0, QlightUnit.dip2px(getContext(), 10), 0, QlightUnit.dip2px(getContext(), 10));
        addView(contentTitle, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addcontentTitleView(contentTitle);
        addListView();
    }

    private void addcontentTitleView(LinearLayout contentTitle) {
        LinearLayout linearLayout1 = new LinearLayout(getContext());
        linearLayout1.setOrientation(HORIZONTAL);
        linearLayout1.setGravity(Gravity.CENTER);
        province_text = new CityTextView(getContext());
        province_text.setTextColor(TextColor);
        province_text.setText("北京");
        province_text.setTextSize(QlightUnit.sp2px(getContext(), 15));
        linearLayout1.addView(province_text);
        city_tx = new CityTextView(getContext());
        city_tx.setTextColor(TextColor);
        city_tx.setText("北京市");
        city_tx.setTextSize(QlightUnit.sp2px(getContext(), 15));
        city_tx.setPadding(20, 0, 0, 0);
        linearLayout1.addView(city_tx);
        contentTitle.addView(linearLayout1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, QlightUnit.dip2px(getContext(), 10), 0, 0);

        area_tx = new CityTextView(getContext());
        area_tx.setText("东城区");
        area_tx.setTextColor(TextColor);
        area_tx.setGravity(Gravity.CENTER);
        area_tx.setTextSize(QlightUnit.sp2px(getContext(), 12));
        contentTitle.addView(area_tx, layoutParams);

        street_tx = new CityTextView(getContext());
        street_tx.setText("东华门街道");
        street_tx.setTextColor(TextColor);
        street_tx.setGravity(Gravity.CENTER);
        street_tx.setTextSize(QlightUnit.sp2px(getContext(), 12));
        contentTitle.addView(street_tx, layoutParams);
    }


    private void addListView() {
        recyclerView = new RecyclerView(getContext());
        addView(recyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, QlightUnit.dip2px(getContext(), 270)));
        LinearLayout bottomView = new LinearLayout(getContext());
        bottomView.setOrientation(HORIZONTAL);
        bottomView.setGravity(Gravity.RIGHT);
        int padding = QlightUnit.dip2px(getContext(), 5);
        bottomView.setPadding(padding, padding, padding, padding);
        LayoutParams btnlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btnlp.setMargins(padding, 0, padding, 0);
        mdtp_cancel = new CityTextView(getContext());
        mdtp_cancel.setTextColors(bgColor);
        mdtp_cancel.setText_press_color(Utils.darkenColor(bgColor));
        mdtp_cancel.commit();
        mdtp_cancel.setText("取消");

        bottomView.addView(mdtp_cancel, btnlp);
        mdtp_sure = new CityTextView(getContext());
        mdtp_sure.setTextColors(bgColor);
        mdtp_sure.setText_press_color(Utils.darkenColor(bgColor));

        mdtp_sure.setText("确定");

        mdtp_sure.setPadding(padding, padding, padding, padding);
        mdtp_sure.commit();
        bottomView.addView(mdtp_sure, btnlp);
        addView(bottomView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public CityView(Context context, int bgColor) {
        super(context);
        this.bgColor = bgColor;
        initUi();
    }
}
