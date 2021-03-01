package com.zyq.ui.picker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyq.ui.QlightUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TimePickerManager {
    public interface PickerViewInterface<T> {
        View getView();

        View getOkView();

        View getCancelView();

        T getValue();

        void setThemeColor(int color);

    }

    public enum FormatState {
        YYYY(-1),
        MM(-1),
        DD(-1),
        HH(-1),
        mm(-1),
        W(-1),
        CC(0);//自定义
        public int point;
        public int jump = 1;

        public FormatState setJump(int jump) {
            this.jump = jump;
            return this;
        }

        FormatState(int point) {
            this.point = point;
        }
    }

    public interface TimePickerViewInterface<T> extends PickerViewInterface<T> {

        void setCustomTimePicker(String lable, FormatState state, List<String> data, int selectedPosition);

        void bindTextView(TextView textView);

        PickerViewInterface simpleShow();
    }

    public class ObjectHolder<T> {
        protected T holderObj;
    }

    public class MaterialTimePickerLayout extends ObjectHolder<Dialog> implements TimePickerViewInterface {
        private Context context;
        private Calendar selectedCalender;
        private int YearStart = 1988, YearEnd = 2050;
        private FormatState[] formatstates;
        private WheelPicker[] pocker = new WheelPicker[5];
        private LinearLayout.LayoutParams pickerLp;
        private LinearLayout.LayoutParams lableLP;
        private int color = -1, content_color = Color.WHITE;
        private LinearLayout contentView;
        private LinearLayout rootView;
        private LinearLayout titleView;
        public LightRichBubbleText sure, cancel;
        private int wheelTextSize = 22;
        private TextView[] lable = new TextView[5];
        private List<CustomerBean> CustomerBeans;

        /**
         * 构造方法
         *
         * @param formatstates 需要加载的Wheel
         * @param context      上下文
         */
        public MaterialTimePickerLayout(Context context, FormatState... formatstates) {
            this(context, new Date(), formatstates);
        }

        /**
         * 构造方法
         *
         * @param date         默认时间
         * @param formatstates 需要加载的Wheel
         * @param context      上下文
         */
        public MaterialTimePickerLayout(Context context, java.util.Date date, FormatState... formatstates) {
            this.context = context;
            this.formatstates = formatstates;
            selectedCalender = Calendar.getInstance();
            if (color == -1) {
                color = Color.RED;
            }
            selectedCalender.setTime(date);
        }

        /**
         * 设置默认时间
         *
         * @param date
         **/
        public void setDate(java.util.Date date) {
            selectedCalender.setTime(date);
        }

        private void initTitleView() {
            int left = QlightUnit.dip2px(context, 25);
            int top = QlightUnit.dip2px(context, 10);
            titleView.setPadding(left, top, left, top);
            sure = new LightRichBubbleText(context);
            sure.setText("确定");
            sure.setText_press_color(0X4CFFFFFF);
            sure.setText_color(Color.WHITE);
            sure.setText_active_color(Color.WHITE);
            sure.commit();
            cancel = new LightRichBubbleText(context);
            cancel.setText("取消");
            cancel.setText_press_color(0X4CFFFFFF);
            cancel.setText_color(Color.WHITE);
            cancel.setText_active_color(Color.WHITE);
            cancel.commit();
            TextView title = new TextView(context);
            title.setText("选择时间");
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(Color.WHITE);
            title.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            titleView.addView(cancel);
            titleView.addView(title, lp);
            titleView.addView(sure);
            titleView.setBackgroundColor(color);
            setListener();
        }

        private void setListener() {
        }

        /**
         * 初始化绑定滚轮监听器
         */
        private void initEvent() {
            for (int i = 0; i < formatstates.length; i++) {
                final int posion = i;
                pocker[i].setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(WheelPicker picker, Object data, int position) {
                        upDataSheel(formatstates[posion], String.valueOf(data));
                    }
                });
            }
        }

        /**
         * 刷新滚轮数据
         *
         * @param value 选中的日期（阿拉伯数字）
         * @param state 当前回调的wheel类型
         */
        private void upDataSheel(FormatState state, String value) {
            switch (state) {
                case YYYY:
                    selectedCalender.set(Calendar.YEAR, Integer.parseInt(value));
                    upDataDD(state);
                    break;
                case MM:
                    selectedCalender.set(Calendar.MONTH, Integer.parseInt(value) - 1);
                    upDataDD(state);
                    break;
                case DD:
                    selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(value));
                    break;
                case HH:
                    selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(value));
                    break;
                case mm:
                    selectedCalender.set(Calendar.MINUTE, Integer.parseInt(value));
                    break;
                case W:
                    selectedCalender.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(value));
                    break;
            }
        }

        /**
         * 刷新天数（关联月，年）
         */
        private void upDataDD(FormatState state) {
            int day = getPosin(FormatState.DD);
            if (day != -1) {
                List<String> items = pocker[day].getData();
                items.clear();
                items.addAll(initDate(1, state.jump, getMaxDayOfMonth(selectedCalender.get(Calendar.MONTH) + 1)));
                pocker[day].setData(items);
                pocker[day].setSelectedItemPosition(0);
                selectedCalender.set(Calendar.DAY_OF_MONTH, 1);
            }
        }

        /**
         * 获取当前选中的时间
         */
        public java.util.Date getTime() {
            return selectedCalender.getTime();
        }

        /**
         * 获取对应滚轮位置
         */
        private int getPosin(FormatState state) {
            for (FormatState item : formatstates) {
                if (item == state) {
                    return item.point;
                }
            }
            return -1;
        }

        /**
         * 控件初始化绘制
         */
        private void init() {
            if (QlightUnit.isEmpty(formatstates)) {
                formatstates = new FormatState[]{FormatState.YYYY, FormatState.MM, FormatState.DD, FormatState.HH, FormatState.mm};
            }
            int left = QlightUnit.dip2px(context, 25);
            int top = QlightUnit.dip2px(context, 20);
            contentView = new LinearLayout(context);
            contentView.setGravity(Gravity.CENTER);
            contentView.setOrientation(LinearLayout.HORIZONTAL);
            contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentView.setPadding(left, top, left, top);
            rootView.addView(contentView);
            contentView.setBackgroundColor(content_color);
            pickerLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, QlightUnit.dip2px(context, 165), 1);
            lableLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < formatstates.length; i++) {
                formatstates[i].point = i;
                prase(formatstates[i]);
            }
            customTimePickerPrase();
        }

        /**
         * 绘制拓展组件
         */
        private void customTimePickerPrase() {
            if (CustomerBeans == null || CustomerBeans.size() < 1)
                return;
            int size = formatstates.length;
            for (CustomerBean item : CustomerBeans) {
                List<FormatState> formatList = new ArrayList<>(Arrays.asList(formatstates));
                formatList.add(item.state);
                formatstates = formatList.toArray(new FormatState[]{});
                pocker[size] = item.wheelPicker;
                contentView.addView(item.wheelPicker, pickerLp);
                contentView.addView(item.textView, lableLP);
                size++;
            }
        }

        /**
         * 绘制头部布局
         */
        private void initHeard() {
            rootView = new LinearLayout(context);
//        rootView.setPadding(50,0,50,0);
            LinearLayout.LayoutParams rootlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rootView.setOrientation(LinearLayout.VERTICAL);
            rootView.setLayoutParams(rootlp);
            titleView = new LinearLayout(context);
            titleView.setOrientation(LinearLayout.HORIZONTAL);
            titleView.setGravity(Gravity.CENTER_VERTICAL);
            initTitleView();
            rootView.addView(titleView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        /**
         * 绘制wheel
         *
         * @param item 需要绘制的类型
         */
        private void prase(FormatState item) {
            switch (item) {
                case YYYY:
                    WheelPicker yy = new WheelPicker(context, color, wheelTextSize);
                    yy.setLayoutParams(pickerLp);
                    contentView.addView(yy);
                    pocker[item.point] = yy;
                    TextView yt = new TextView(context);
                    yt.setText("年");
                    yt.setLayoutParams(lableLP);
                    contentView.addView(yt);
                    lable[item.point] = yt;
                    yy.setData(initDate(YearStart, item.jump, (YearEnd - YearStart)));
                    yy.setSelectedItemPosition(String.valueOf(selectedCalender.get(Calendar.YEAR)));
                    break;
                case MM:
                    WheelPicker mm = new WheelPicker(context, color, wheelTextSize);
                    mm.setLayoutParams(pickerLp);
                    contentView.addView(mm);
                    pocker[item.point] = mm;
                    TextView mt = new TextView(context);
                    mt.setText("月");
                    mt.setLayoutParams(lableLP);
                    contentView.addView(mt);
                    lable[item.point] = mt;
                    mm.setData(initDate(1, item.jump, 12));
                    mm.setSelectedItemPosition(selectedCalender.get(Calendar.MONTH));
                    break;
                case DD:
                    WheelPicker dd = new WheelPicker(context, color, wheelTextSize);
                    dd.setLayoutParams(pickerLp);
                    contentView.addView(dd);
                    pocker[item.point] = dd;
                    TextView dt = new TextView(context);
                    dt.setText("日");
                    dt.setLayoutParams(lableLP);
                    contentView.addView(dt);
                    lable[item.point] = dt;
                    dd.setData(initDate(1, item.jump, selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH)));
                    dd.setSelectedItemPosition(selectedCalender.get(Calendar.DAY_OF_MONTH) - 1);
                    break;
                case HH:
                    WheelPicker hh = new WheelPicker(context, color, wheelTextSize);
                    hh.setLayoutParams(pickerLp);
                    contentView.addView(hh);
                    pocker[item.point] = hh;
                    TextView ht = new TextView(context);
                    ht.setText("时");
                    ht.setLayoutParams(lableLP);
                    contentView.addView(ht);
                    lable[item.point] = ht;
                    hh.setData(initDate(0, item.jump, 24));
                    hh.setSelectedItemPosition(selectedCalender.get(Calendar.HOUR_OF_DAY));
                    break;
                case mm:
                    WheelPicker minute = new WheelPicker(context, color, wheelTextSize);
                    minute.setLayoutParams(pickerLp);
                    contentView.addView(minute);
                    pocker[item.point] = minute;
                    TextView tminute = new TextView(context);
                    tminute.setText("分");
                    tminute.setLayoutParams(lableLP);
                    contentView.addView(tminute);
                    lable[item.point] = tminute;
                    minute.setData(initDate(0, item.jump, 60));
                    minute.setSelectedItemPosition(selectedCalender.get(Calendar.MINUTE));
                    break;
                case W:
                    WheelPicker week = new WheelPicker(context, color, wheelTextSize);
                    week.setLayoutParams(pickerLp);
                    contentView.addView(week);
                    pocker[item.point] = week;
                    TextView tweek = new TextView(context);
                    tweek.setText("周");
                    tweek.setLayoutParams(lableLP);
                    contentView.addView(tweek);
                    lable[item.point] = tweek;
                    week.setData(initDate(1, item.jump, selectedCalender.getActualMaximum(Calendar.WEEK_OF_YEAR)));
                    week.setSelectedItemPosition(selectedCalender.get(Calendar.WEEK_OF_YEAR) / item.jump);
                    break;
            }
        }

        /**
         * @param color 顔色非资源
         */
        @Override
        public void setThemeColor(int color) {
            this.color = color;
        }

        /**
         * 自定义滚轮接口
         *
         * @param lable            名称
         * @param state            对应的时间模块
         * @param data             滚轮数据（注意数据必须为有效的日期数据）
         * @param selectedPosition 默认选中的数据
         */
        @Override
        public void setCustomTimePicker(String lable, FormatState state, List data, int selectedPosition) {
            if (CustomerBeans == null)
                CustomerBeans = new ArrayList<>();
            CustomerBean customerBean = new CustomerBean();
            WheelPicker yy = new WheelPicker(context, color, wheelTextSize);
            TextView yt = new TextView(context);
            yt.setText(lable);
            yy.setData(data);
            yy.setSelectedItemPosition(selectedPosition);
            customerBean.state = state;
            customerBean.textView = yt;
            customerBean.wheelPicker = yy;
            CustomerBeans.add(customerBean);
        }

        @Override
        public View getView() {
            if (contentView == null) {
                initHeard();
                init();
                initEvent();
            }
            return rootView;
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
            return getTime();
        }

        /**
         * 获取范围数据
         *
         * @param start_item 开始位置
         * @param count      数量
         */
//    private List<String> initDate(int start_item, int count) {
//      return initDate(start_item,1 ,count);
//    }
        private List<String> initDate(int start_item, int jump, int count) {
            List<String> data = new ArrayList<>();
            int i = 0;
            while (i < count) {
                int item = start_item + i;
                if (item > 9) {
                    data.add("" + item);
                } else {
                    data.add("0" + item);
                }
                i += jump;
            }
            return data;
        }

        /**
         * 获取每个月的最大值
         *
         * @param arg 月份
         */
        private int getMaxDayOfMonth(int arg) {
            switch (arg) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    return 31;
                case 4:
                case 6:
                case 9:
                case 11:
                    return 30;
                case 2:
                    int year = selectedCalender.get(Calendar.YEAR);
                    if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                        return 29;
                    } else {
                        return 28;
                    }
                default:
                    return 31;
            }
        }

//        @Override
//        public MaterialTimePickerLayout simPleShow() {
//            holderObj = SimpleDialog.MakeDialog(getView(), LightDialog.QGriavty.BOTTOM);
//
//            getCancelView().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holderObj.dismiss();
//                }
//            });
//            getOkView().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holderObj.dismiss();
//                }
//            });
//            holderObj.show();
//            return this;
//        }

        @Override
        public void bindTextView(TextView textView) {

        }

        @Override
        public PickerViewInterface simpleShow() {
            return null;
        }


    }

    /**
     * 拓展对象
     */
    public static class CustomerBean {
        WheelPicker wheelPicker;
        TextView textView;
        FormatState state;
    }
}
