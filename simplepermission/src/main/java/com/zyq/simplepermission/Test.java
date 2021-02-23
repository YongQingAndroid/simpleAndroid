package com.zyq.simplepermission;

import android.content.SharedPreferences;

import java.util.List;

public class Test {

    void test() {
       QPermissions.with(null).permission().request(new OnPermission() {
           @Override
           public void hasPermission(List<String> granted, boolean all) {

           }

           @Override
           public void noPermission(List<String> denied, boolean never) {
               if(never){
                   QPermissions.startPermissionActivity(null, denied);
               }
           }
       });
    }
}
