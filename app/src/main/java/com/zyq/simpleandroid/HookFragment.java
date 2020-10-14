package com.zyq.simpleandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.zyq.simpleandroid.bean.CommonBean;
import com.zyq.simpleandroid.request.CheckPermission;
import com.zyq.simpleandroid.request.IRequestPermissions;
import com.zyq.simpleandroid.request.RequestPermissions;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


@SuppressLint("ValidFragment")
public class HookFragment extends Fragment {
    View rootView;
    Activity mactivity;
    IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求
    public IRequestPermissionsResult requestPermissionsResult = RequestPermissionsResultSetApp.getInstance();//动态权限请求结果处理
    boolean ready = false;
    LinkedHashMap<String, RequestItem> permissMap = new LinkedHashMap<>();
    CommonBean<SettingCall, String> mSettingCall = new CommonBean<>();

    @SuppressLint("ValidFragment")
    public HookFragment(Activity activity) {
        this.mactivity = activity;
    }
    @SuppressLint("ValidFragment")
    public  HookFragment() {
    }
    public Activity getThisActivity() {
        if (getActivity() == null) {
            return mactivity;
        }
        return getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            rootView = new LinearLayout(getThisActivity());
        } else {
            rootView = new LinearLayout(container.getContext());
        }

        return rootView;
    }

    //请求权限
    public boolean requestPermissions(String[] permissions, PermissionCallBack permissionCallBack) {
        boolean flag = requestPermissions.requestAllPermission(getThisActivity(), permissions);
        if (!flag) {
            RequestItem item = new RequestItem(permissionCallBack, CheckPermission.getDisAllowedPermissions(getThisActivity(), permissions));
            permissMap.put(String.valueOf(item.mResultCode), item);
            executeTask();
        }
        return flag;
    }

    //请求权限
    private boolean realRequestPermissions(RequestItem requestItem) {
        //开始请求权限
        return requestPermissions.requestPermissions(
                this,
                getThisActivity(),
                requestItem
        );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ready = true;
        executeTask();
    }

    public void goSetting(String[] arg, SettingCall settingCall) {
        mSettingCall.setObj(settingCall);
        mSettingCall.setObjArry(arg);
        String name = PermissionUtils.getInstance().getPermissionNames(arg);
        openAppDetails(name);
    }

    public interface SettingCall {
        void result(boolean arg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSettingCall.getObj() != null && requestCode == 999) {
            boolean flag = requestPermissions.requestAllPermission(getThisActivity(), mSettingCall.getObjArry());
            mSettingCall.getObj().result(flag);
        }
        mSettingCall.setObj(null);
    }

    public void openAppDetails(String permissionNames) {
        StringBuilder sb = new StringBuilder();
        sb.append(PermissionUtils.PermissionTip1);
        sb.append(permissionNames);
        sb.append(PermissionUtils.PermissionTip2);
        AlertDialog.Builder builder = new AlertDialog.Builder(getThisActivity());
        builder.setMessage(sb.toString());
        builder.setPositiveButton(PermissionUtils.PermissionDialogPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getThisActivity().getPackageName()));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivityForResult(intent, 999);
            }
        });
        builder.setNegativeButton(PermissionUtils.PermissionDialogNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSettingCall.getObj().result(false);
            }
        });
        builder.show();
    }

    public void executeTask() {
        if (ready && permissMap.size() > 0) {
            String mapKey = permissMap.keySet().iterator().next();
            realRequestPermissions(permissMap.get(mapKey));

        }
    }
    PermissionResult result=null;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      final   RequestItem requestItem = permissMap.get(String.valueOf(requestCode));
        if (requestItem != null && requestItem.getCallback() != null) {
            if (permissions != null && permissions.length > 0) {
                result=requestPermissionsResult.getPermissionResult(getThisActivity(), permissions, grantResults);
            } else {
                 result= requestPermissionsResult.getPermissionResult(getThisActivity(), requestItem.permissions);
            }
            if(result.state==State.FAIL){
               String[] fail= result.state.noAskFailPermissions(mactivity);
               if(fail!=null&&fail.length>0){
                      goSetting(fail, arg -> {
                          if(arg){
                              State state=State.DONE;
                              state.setPermission( result.state.getPermission());
                              result.state=state;
                          }
                          if(requestItem!=null&&requestItem.getCallback()!=null){
                              requestItem.getCallback().over(result);
                          }
                      });
               }else{
                   if(requestItem!=null&&requestItem.getCallback()!=null){
                       requestItem.getCallback().over(result);
                       requestItem.callback = null;
                   }
               }
            }else{
                if(requestItem!=null&&requestItem.getCallback()!=null){
                requestItem.getCallback().over(result);
                requestItem.callback = null;
                }
            }
        }
        permissMap.remove(String.valueOf(requestCode));

        executeTask();
    }

    public static class RequestItem {
        SoftReference<PermissionCallBack> callback;
        String[] permissions;
        int mResultCode;

        RequestItem(PermissionCallBack callback, String[] permissions) {
            this.permissions = permissions;
            this.callback = new SoftReference<>(callback);
            mResultCode = PermissionUtils.getResultCode();
        }

        public PermissionCallBack getCallback() {
            if (callback == null)
                return null;
            return callback.get();
        }

        public String[] getPermissions() {
            return permissions;
        }

        public int getmResultCode() {
            return mResultCode;
        }
    }
}
