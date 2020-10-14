package com.zyq.simpleandroid;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cmic.sso.sdk.AuthThemeConfig;
import com.cmic.sso.sdk.auth.AuthnHelper;
import com.cmic.sso.sdk.auth.BackPressedListener;
import com.cmic.sso.sdk.auth.TokenListener;

import org.json.JSONObject;

public class AutoLoginManager {
    private String APPID = "300011995656", APPKEY = "E85BBEEB9CBAD8E05695F35070838527";
    private static AutoLoginManager self;
    public TokenListener mTokenListener;
    AuthThemeConfig themeConfigBuilder = new AuthThemeConfig.Builder()
            .setStatusBar(0xff0086d0, false)//状态栏颜色、字体颜色
            .setNavTextSize(20)
            .setNavTextColor(0xff0085d0)//导航栏字体颜色

            .setNumberSize(20)////手机号码字体大小
            .setNumberColor(0xff333333)//手机号码字体颜色
            .setNumberOffsetX(30)//号码栏X偏移量
            .setNumFieldOffsetY_B(100)
            .setNumFieldOffsetY(100)//号码栏Y偏移量
            .setLogBtnText("本机号码一键登录")//登录按钮文本
            .setLogBtnTextColor(0xffffffff)//登录按钮文本颜色
            .setLogBtnImgPath("umcsdk_login_btn_bg")//登录按钮背景

            .setLogBtnText(" ", 0xffffffff, 15)
            .setLogBtnOffsetY_B(200)//登录按钮Y偏移量
            .setLogBtnOffsetY(200)//登录按钮Y偏移量
//                .setLogBtn(500,30)
            .setLogBtnMargin(30, 30)
            .setBackPressedListener(new BackPressedListener() {
                @Override
                public void onBackPressed() {

                }
            }).build();

    AutoLoginManager() {
        init();

        AuthnHelper.setDebugMode(true);
    }

    public static AutoLoginManager getInstance() {
        if (self == null) {
            self = new AutoLoginManager();
        }
        return self;
    }

    public void goLogin(Context context) {
        getHelper(context).setAuthThemeConfig(themeConfigBuilder);
        getHelper(context).getPhoneInfo(APPID, APPKEY, mTokenListener);
    }
    public void getToken(Context context) {
        getHelper(context).loginAuth(APPID, APPKEY, mTokenListener,6666);
    }
    public AuthnHelper getHelper(Context context) {
        return AuthnHelper.getInstance(context);
    }

    public void init() {
        mTokenListener = (SDKRequestCode, jObj) -> {
            if (jObj != null) {
                String mResultString = jObj.toString();
                if (jObj.has("token")) {
                    String mtoken = jObj.optString("token");
                }
                Log.i("qing", jObj.toString());
            }
        };
    }
}
