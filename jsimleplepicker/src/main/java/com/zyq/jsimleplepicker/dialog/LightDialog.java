package com.zyq.jsimleplepicker.dialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.zyq.jsimleplepicker.QlightUnit;

/**
 * 提示框处理类
 * 作者：zyq on 2017/3/1 14:48
 * 邮箱：zyq@posun.com
 */
public class LightDialog extends Dialog {
    private QGriavty state;
    private boolean isFullSrceen = false;
    protected Window window;
    protected boolean hasPadding=false;
    public LightDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    public LightDialog(Context context) {
        super(context);
        init();
    }

    public void setHasPadding(boolean hasPadding) {
        this.hasPadding = hasPadding;
    }

    private void init() {
        this.setCancelable(false);
        window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initEvent();
    }
    public void setFullSrceen(boolean arg) {
        isFullSrceen = arg;
    }

    @Override
    public void show() {

        super.show();
        /**這些东西不写在show后面变不会生效*/
        if(!hasPadding){
            WindowManager.LayoutParams lp = window.getAttributes();
            if (isFullSrceen) {
                lp.dimAmount = 0f;
            }
            window.setBackgroundDrawableResource(android.R.color.transparent);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            if(QGriavty.FULL==state)
                return;
        }
        window.setGravity(state == null ? Gravity.BOTTOM : state.state);
    }

    public void setGravity(QGriavty arg) {
        state = arg;
    }

    public static LightDialog MakeDialog(View content, QGriavty... state) {
        LightDialog dialog = new LightDialog(content.getContext());
        dialog.setContentView(content);
        if (!QlightUnit.isEmpty(state)) {
            dialog.setGravity(state[0]);
        }
        return dialog;
    }

    private void initEvent() {
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    LightDialog.this.cancel();
                }
                return false;
            }
        });
    }

    public enum QGriavty {
        TOP(Gravity.TOP),
        CENTER(Gravity.CENTER),
        FULL(0),
        BOTTOM(Gravity.BOTTOM), ;

        protected int state;
        QGriavty(int state) {
            this.state = state;
        }
    }
}
