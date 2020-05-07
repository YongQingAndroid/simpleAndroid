package com.zyq.jsimleplepicker.citypicker;

import android.graphics.Color;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.zyq.jsimleplepicker.QlightUnit;
import com.zyq.jsimleplepicker.textView.LightRichBubbleText;

import androidx.recyclerview.widget.RecyclerView;


/**
 * package kotlinTest:com.qing.lightview.material.adapter.CityAdapter.class
 * 作者：zyq on 2017/8/3 14:44
 * 邮箱：zyq@posun.com
 */
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.Holder> {
    private CityDataArrayList list;
    private int selectPosition = 0;
    private Listener listener;
    private int themeColor = -1;
    private int padding = 0;

    public CityAdapter(CityDataArrayList list, int themeColor) {
        this.list = list;
        this.themeColor = themeColor;
//        this.themeColor = Color.parseColor("#00796b");
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (padding == 0)
            padding = QlightUnit.dip2px(parent.getContext(), 10);
        LightRichBubbleText view = new LightRichBubbleText(parent.getContext());
        view.setText_active_color(themeColor);
        view.setText_press_color(themeColor);
        view.setText_color(Color.BLACK);
        view.setGravity(Gravity.CENTER);
        view.setPadding(padding, padding, padding, padding);
//        view.setBg_press_color(Color.LTGRAY);
//        view.setRadio(QlightUnit.dip2px(parent.getContext(), 20));
        view.resitTextTextColor();
        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new Holder(view);
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (position == selectPosition) {
            holder.provinces_item.setChecked(true);
        } else {
            holder.provinces_item.setChecked(false);
        }
        holder.provinces_item.setText(list.get(position).getName());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        LightRichBubbleText provinces_item;

        public Holder(LightRichBubbleText itemView) {
            super(itemView);
            provinces_item = itemView;
            provinces_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onItemClick(getAdapterPosition(), view);
                }
            });
        }
    }

    public interface Listener {
        void onItemClick(int position, View view);
    }
}
