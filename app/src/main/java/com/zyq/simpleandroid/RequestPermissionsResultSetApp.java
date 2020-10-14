package com.zyq.simpleandroid;

import android.app.Activity;
import android.content.pm.PackageManager;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * 类：RequestPermissionsResult
 * 处理权限申请的结果，如果未允许，提示用户,并跳转至设置APP权限页面
 * 作者： zyq
 */
public class RequestPermissionsResultSetApp implements IRequestPermissionsResult {
    private static RequestPermissionsResultSetApp requestPermissionsResult;

    public static RequestPermissionsResultSetApp getInstance() {
        if (requestPermissionsResult == null) {
            requestPermissionsResult = new RequestPermissionsResultSetApp();
        }
        return requestPermissionsResult;
    }

    @Override
    public PermissionResult getPermissionResult(Activity activity, @NonNull String[] permissions) {
        List<String> allow = new ArrayList<>();
        List<String> disAllow = new ArrayList<>();
        for (int index = 0; index < permissions.length; index++) {
            String permission = permissions[index];
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                disAllow.add(permission);
            } else {
                allow.add(permission);
            }
        }
        PermissionResult result = new PermissionResult(allow.toArray(new String[]{}), disAllow.toArray(new String[]{}));
        return result;
    }

    @Override
    public PermissionResult getPermissionResult(Activity activity, @NonNull String[] permissions, @NonNull int[] grantResults) {
        List<String> allow = new ArrayList<>();
        List<String> disAllow = new ArrayList<>();
        for (int index = 0; index < grantResults.length; index++) {
            int grant = grantResults[index];
            if (grant != PackageManager.PERMISSION_GRANTED) {
                disAllow.add(permissions[index]);
            } else {
                allow.add(permissions[index]);
            }
        }
        PermissionResult result = new PermissionResult(allow.toArray(new String[]{}), disAllow.toArray(new String[]{}));
        return result;
    }


}
