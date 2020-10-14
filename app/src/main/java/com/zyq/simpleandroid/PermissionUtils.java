package com.zyq.simpleandroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类：PermissionUtils
 * 作者： zyq
 */
public class PermissionUtils {
    public static AtomicInteger atomicInteger = new AtomicInteger(10000);
    public static String PermissionTip1 = "提示 \n\n金斧子基金部分功能需要请求您的手机权限，请允许以下权限：\n\n";//权限提醒
    public static String PermissionTip2 = "\n请到 “应用信息 -> 权限” 中授予！";//权限提醒
    public static String PermissionDialogPositiveButton = "去手动授权";
    public static String PermissionDialogNegativeButton = "取消";

    public static int getResultCode() {
        return atomicInteger.addAndGet(1);
    }

    private static PermissionUtils permissionUtils;

    public static PermissionUtils getInstance() {
        if (permissionUtils == null) {
            permissionUtils = new PermissionUtils();
        }
        return permissionUtils;
    }

    private HashMap<String, String> permissions;

    public HashMap<String, String> getPermissions() {
        if (permissions == null) {
            permissions = new HashMap<>();
            initPermissions();
        }
        return permissions;
    }

    private void initPermissions() {
        //联系人/通讯录权限
        permissions.put("android.permission.WRITE_CONTACTS", " 通讯录/联系人");
        permissions.put("android.permission.GET_ACCOUNTS", " 通讯录/联系人");
        permissions.put("android.permission.READ_CONTACTS", " 通讯录/联系人");
        //电话权限
        permissions.put("android.permission.READ_CALL_LOG", " 电话");
        permissions.put("android.permission.READ_PHONE_STATE", " 电话");
        permissions.put("android.permission.CALL_PHONE", " 电话");
        permissions.put("android.permission.WRITE_CALL_LOG", " 电话");
        permissions.put("android.permission.USE_SIP", " 电话");
        permissions.put("android.permission.PROCESS_OUTGOING_CALLS", " 电话");
        permissions.put("com.android.voicemail.permission.ADD_VOICEMAIL", " 电话");
        //日历权限
        permissions.put("android.permission.READ_CALENDAR", " 日历");
        permissions.put("android.permission.WRITE_CALENDAR", " 日历");
        //相机拍照权限
        permissions.put("android.permission.CAMERA", " 相机/拍照");
        //传感器权限
        permissions.put("android.permission.BODY_SENSORS", " 传感器");
        //定位权限
        permissions.put("android.permission.ACCESS_FINE_LOCATION", " 定位");
        permissions.put("android.permission.ACCESS_COARSE_LOCATION", " 定位");
        //文件存取
        permissions.put("android.permission.READ_EXTERNAL_STORAGE", " 文件存储");
        permissions.put("android.permission.WRITE_EXTERNAL_STORAGE", " 文件存储");
        //音视频、录音权限
        permissions.put("android.permission.RECORD_AUDIO", " 音视频/录音");
        //短信权限
        permissions.put("android.permission.READ_SMS", " 短信");
        permissions.put("android.permission.RECEIVE_WAP_PUSH", " 短信");
        permissions.put("android.permission.RECEIVE_MMS", " 短信");
        permissions.put("android.permission.RECEIVE_SMS", " 短信");
        permissions.put("android.permission.SEND_SMS", " 短信");
        permissions.put("android.permission.READ_CELL_BROADCASTS", " 短信");
    }

    /**
     * 获得权限名称集合（去重）
     *
     * @param permission 权限数组
     * @return 权限名称
     */
    public String getPermissionNames(String[] permission) {
        if (permission == null || permission.length == 0) {
            return "\n";
        }
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>();
        HashMap<String, String> permissions = getPermissions();
        for (int i = 0; i < permission.length; i++) {
            String name = permissions.get(permission[i]);
            if (name != null && !list.contains(name)) {
                list.add(name);
                sb.append(name);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
