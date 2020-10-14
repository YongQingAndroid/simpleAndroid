package com.zyq.simpleandroid.request;

import android.app.Activity;
import android.os.Build;

import com.zyq.simpleandroid.HookFragment;

import java.util.List;

import androidx.fragment.app.Fragment;


/**
 * 类：RequestPermissions
 * 作者： zyq
 */
public class RequestPermissions implements IRequestPermissions {
    private static RequestPermissions requestPermissions;

    public static RequestPermissions getInstance() {
        if (requestPermissions == null) {
            requestPermissions = new RequestPermissions();
        }
        return requestPermissions;
    }

    @Override
    public boolean requestPermissions(Activity activity, HookFragment.RequestItem requestItem) {
        return false;
    }

    @Override
    public boolean requestPermissions(Fragment fragment, Activity activity, HookFragment.RequestItem requestItem) {
        //判断并请求权限
        return requestNeedPermission(fragment, activity, requestItem);
    }

    @Override
    public boolean requestAllPermission(Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        //判断是否已赋予了全部权限
        boolean isAllGranted = CheckPermission.checkPermissionAllGranted(activity, permissions);
        if (isAllGranted) {
            return true;
        }
        return false;
    }

    private boolean requestNeedPermission(Fragment fragment, Activity activity, HookFragment.RequestItem requestItem) {
        List<String> list = CheckPermission.checkPermissionDenied(activity, requestItem.getPermissions());
        if (list.size() == 0) {
            return true;
        }

        //请求权限
        String[] deniedPermissions = list.toArray(new String[list.size()]);
        fragment.requestPermissions(deniedPermissions, requestItem.getmResultCode());
        return false;
    }

}
