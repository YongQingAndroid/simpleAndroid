package com.zyq.ui.camare;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.util.Log;

import java.util.Calendar;

public class SensorControler implements IActivityLifiCycle, SensorEventListener {
    public static final String TAG = "SensorControler";
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private int mX, mY, mZ;
    private long lastStaticStamp = 0;
    Calendar mCalendar;

    public static final int DELEY_DURATION = 1000;

    public static final int STATUS_NONE = 0;
    public static final int STATUS_STATIC = 1;
    public static final int STATUS_MOVE = 2;
    public boolean locked=false;
    private int STATUE = STATUS_NONE;

    private CameraFocusListener mCameraFocusListener;

    private static SensorControler mInstance;

    private int foucsing = 1;  //1 表示没有被锁定 0表示被锁定

    private SensorControler(Context application) {
        mSensorManager = (SensorManager) application.getSystemService(Activity.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
    }

    public static SensorControler getInstance(Context application) {
        if (mInstance == null) {
            mInstance = new SensorControler(application);
        }
        return mInstance;
    }

    public void setCameraFocusListener(CameraFocusListener mCameraFocusListener) {
        this.mCameraFocusListener = mCameraFocusListener;
    }

    @Override
    public void onStart() {
        restParams();
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop() {
        mSensorManager.unregisterListener(this, mSensor);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    long time=0;
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }
       if(locked)
           return;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            mCalendar = Calendar.getInstance();
            long stamp = mCalendar.getTimeInMillis();// 1393844912


            if (STATUE != STATUS_NONE) {
                int px = Math.abs(mX - x);
                int py = Math.abs(mY - y);
                int pz = Math.abs(mZ - z);
                double value = Math.sqrt(px * px + py * py + pz * pz);
                if (value > 1.4) {
                    Log.i(TAG,"检测手机在移动..");
                    STATUE = STATUS_MOVE;
                } else {
                    if (STATUE == STATUS_MOVE) {
                        lastStaticStamp = stamp;
                    }
                        if (stamp - lastStaticStamp > DELEY_DURATION) {
                            //移动后静止一段时间，可以发生对焦行为
                            if(System.currentTimeMillis()-time>1000*5){
                                time=System.currentTimeMillis();
                            }else {
                                return;
                            }
                                if (!locked&& mCameraFocusListener != null) {
                                    mCameraFocusListener.onFocus();
                                    Log.i(TAG,"mobile focusing");
                                    locked=true;
                            }
                        }

                    STATUE = STATUS_STATIC;
                }
            } else {
                lastStaticStamp = stamp;
                STATUE = STATUS_STATIC;
            }

            mX = x;
            mY = y;
            mZ = z;
        }
    }

    private void restParams() {
        STATUE = STATUS_NONE;
        mX = 0;
        mY = 0;
        mZ = 0;
        locked=false;
    }


    public void restFoucs() {
        foucsing = 1;
    }

    public interface CameraFocusListener {
        void onFocus();
    }
}
