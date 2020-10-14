package com.zyq.simpleandroid;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 类：SimplePermission
 * 作者： zyq
 */
public class SimplePermission {
    HookFragment hookFragment;

    public boolean requestPermissions(AppCompatActivity activity, String... permission) {
        return requestPermissions(activity, null, permission);
    }

    public void goSetting(AppCompatActivity activity, String[] arg, HookFragment.SettingCall settingCall) {
        initFragment(activity);
        hookFragment.goSetting(arg, settingCall);
    }

    public IRequestPermissionsResult getResultPrase() {
        if (hookFragment == null) {
            return RequestPermissionsResultSetApp.getInstance();
        }
        return hookFragment.requestPermissionsResult;
    }

    public boolean requestPermissions(AppCompatActivity activity, PermissionCallBack permissionCallBack, String... permission) {
        initFragment(activity);
        return hookFragment.requestPermissions(permission, permissionCallBack);
    }

    private void initFragment(AppCompatActivity activity) {
        if (hookFragment == null) {
            hookFragment = new HookFragment(activity);
            activity.getSupportFragmentManager().beginTransaction().add(hookFragment, "hookFragment").commit();
        }
    }

}
