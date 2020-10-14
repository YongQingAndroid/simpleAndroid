package com.zyq.simpleandroid;

import android.app.Activity;

import androidx.annotation.NonNull;


/**
 * 类：IRequestPermissionsResult
 * 作者： zyq
 */
public interface IRequestPermissionsResult {
    /**
     * 处理权限请求结果
     *
     * @param activity
     * @return 处理权限结果如果全部通过，返回true；否则，引导用户去授权页面
     */
     PermissionResult getPermissionResult(Activity activity, @NonNull String[] permissions);
    PermissionResult getPermissionResult(Activity activity, @NonNull String[] permissions, @NonNull int[] grantResults);
}
