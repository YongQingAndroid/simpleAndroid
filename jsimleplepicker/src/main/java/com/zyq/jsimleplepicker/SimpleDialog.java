package com.zyq.jsimleplepicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.zyq.jsimleplepicker.dialog.LightDialog;

public class SimpleDialog extends LightDialog {
    public SimpleDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public SimpleDialog(Context context) {
        super(context);

    }

    public SimpleDialog bindEvent(int layoutId) {
        return this;
    }

    public static SimpleDialog MakeDialog(View content, QGriavty... state) {
        SimpleDialog dialog = new SimpleDialog(content.getContext());
        dialog.setContentView(content);
        if (!QlightUnit.isEmpty(state)) {
            dialog.setGravity(state[0]);
        }
        return dialog;
    }

    public static class SimpleDialogBuilder {
        Context context;
        View contentView;

        public SimpleDialogBuilder(Context context) {
            this.context = context;
        }

        public SimpleDialogBuilder setContentView(View contentView) {
            this.contentView = contentView;
            return this;
        }

        public SimpleDialogBuilder setContentView(int layoutId) {
            return setContentView(LayoutInflater.from(context).inflate(layoutId, null));
        }

        public SimpleDialog creat() {
            return SimpleDialog.MakeDialog(contentView, QGriavty.CENTER);
        }


    }
}
