package com.zyq.simpleandroid.request;

import android.app.Activity;

import com.zyq.simpleandroid.HookFragment;

import androidx.fragment.app.Fragment;


/**
 * 类：IRequestPermissions 申请权限
 * 作者： zyq
 */
public interface IRequestPermissions {
    /**
     * 请求权限
     *
     * @param activity    上下文
     * @return 如果权限已全部允许，返回true; 反之，请求权限，在
     */
    boolean requestPermissions(Activity activity, HookFragment.RequestItem requestItem);

    boolean requestPermissions(Fragment fragment, Activity activity, HookFragment.RequestItem requestItem);

     boolean requestAllPermission(Activity activity, String[] permissions);
}
