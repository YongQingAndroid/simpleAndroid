package com.zyq.simpleandroid;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;

public enum State {
    DONE(0),
    FAIL(1);
    int arg;
    String[] permission, fail;

    State(int arg) {
        this.arg = arg;
    }

    public void setPermission(String[] permission) {
        this.permission = permission;
    }

    public String[] getFail() {
        return fail;
    }

    public void setFail(String[] fail) {
        this.fail = fail;
    }

    public String[] getPermission() {
        return permission;
    }

    public String[] noAskFailPermissions(Activity activity) {
        if (arg == 0 || fail == null || fail.length < 1) {
            return null;
        }
        List<String> deniedPermission = new ArrayList<>();
        //如果选择了“不再询问”，则弹出“权限指导对话框”
        for (int i = 0; i < fail.length; i++) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, fail[i])) {
                deniedPermission.add(fail[i]);
            }
        }
        return deniedPermission.toArray(new String[]{});
    }
    public int getValue() {
        return arg;
    }
}
