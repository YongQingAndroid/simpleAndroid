package com.zyq.ui.camare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zyq.simplestore.log.LightLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.RequiresApi;

public class CameraView extends FrameLayout implements View.OnClickListener {
    private CameraPreview cameraPreview;
    private OverCameraView mOverCameraView;
    private Camera mCamera;
    private boolean isFoucing;
    private LinearLayout mPhotoLayout, mConfirmLayout;
    private View maskView, tackPhotoView;//遮罩
    private CameraDrawable cameraDrawable;
    private boolean isTakePhoto;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private CameraCall cameraCall;

    public void setCameraCall(CameraCall cameraCall) {
        this.cameraCall = cameraCall;
    }

    //    /**
//     * 图片流暂存
//     */
    private byte[] imageData;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public CameraView(Context context) {
        super(context);
        initUi();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initUi() {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;

        mPhotoLayout = new LinearLayout(getContext());
        mPhotoLayout.setLayoutParams(lp);
        mPhotoLayout.setGravity(Gravity.CENTER);
        mPhotoLayout.setOrientation(LinearLayout.HORIZONTAL);
        tackPhotoView = new View(getContext());
        tackPhotoView.setBackground(new ColorDrawable(Color.RED));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);

        tackPhotoView.setOnClickListener(this);
        mPhotoLayout.addView(tackPhotoView, layoutParams);


        mConfirmLayout = new LinearLayout(getContext());
        mConfirmLayout.setLayoutParams(lp);
        mConfirmLayout.setOrientation(LinearLayout.HORIZONTAL);
        mConfirmLayout.setVisibility(View.GONE);
        Button cancel = new Button(getContext());
        cancel.setText("取消");
        cancel.setOnClickListener(view -> {
            cancleSavePhoto();
        });

        Button submit = new Button(getContext());
        submit.setText("保存");
        submit.setOnClickListener(view -> {
            savePhoto();
        });
        mConfirmLayout.addView(cancel);
        mConfirmLayout.addView(submit);

    }

    private void cancleSavePhoto() {
        mPhotoLayout.setVisibility(View.VISIBLE);
        mConfirmLayout.setVisibility(View.GONE);
        //开始预览
        mCamera.startPreview();
        imageData = null;
        isTakePhoto = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startCamera() {
        if (mCamera == null) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (cameraPreview == null) {
            cameraDrawable = new CameraDrawable(getContext());
            cameraPreview = new CameraPreview(getContext(), mCamera);
            maskView = new View(getContext());
            maskView.setBackground(cameraDrawable);

            mOverCameraView = new OverCameraView(getContext());
            this.addView(cameraPreview);
            this.addView(mOverCameraView);
            this.addView(maskView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            this.addView(mPhotoLayout);
            this.addView(mConfirmLayout);

        } else {
            mCamera.startPreview();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCamera == null)
            super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isFoucing) {
                float x = event.getX();
                float y = event.getY();
                isFoucing = true;
                if (mCamera != null && !isTakePhoto) {
                    mOverCameraView.setTouchFoucusRect(mCamera, autoFocusCallback, x, y);
                }
                mRunnable = () -> {
                    Toast.makeText(getContext(), "自动聚焦超时,请调整合适的位置拍摄！", Toast.LENGTH_SHORT);
                    isFoucing = false;
                    mOverCameraView.setFoucuing(false);
                    mOverCameraView.disDrawTouchFocusRect();
                };
                //设置聚焦超时
                mHandler.postDelayed(mRunnable, 3000);
            }
        }
        return super.onTouchEvent(event);
    }

    //    /**
//     * 注释：自动对焦回调
//     * 时间：2019/3/1 0001 10:02
//     * 作者：郭翰林
//     */
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            isFoucing = false;
            mOverCameraView.setFoucuing(false);
            mOverCameraView.disDrawTouchFocusRect();
            //停止聚焦超时回调
            mHandler.removeCallbacks(mRunnable);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        takePhoto();
    }

    private void savePhoto() {
        FileOutputStream fos = null;
        String cameraPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "DCIM" + File.separator + "Camera";
        //相册文件夹
        File cameraFolder = new File(cameraPath);
        if (!cameraFolder.exists()) {
            cameraFolder.mkdirs();
        }
        //保存的图片文件
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String imagePath = cameraFolder.getAbsolutePath() + File.separator + "IMG_" + simpleDateFormat.format(new Date()) + ".jpg";
        File imageFile = new File(imagePath);
        try {
            fos = new FileOutputStream(imageFile);
            fos.write(imageData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    Bitmap retBitmap = BitmapFactory.decodeFile(imagePath);
                    retBitmap = BitmapUtils.setTakePicktrueOrientation(Camera.CameraInfo.CAMERA_FACING_BACK, retBitmap);

                    BitmapUtils.saveBitmap(BitmapUtils.cropCardBitmap(retBitmap), imagePath);
                    if (cameraCall != null)
                        cameraCall.call(true, imagePath);
                    LightLog.i(imagePath);
                    Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    cameraCall.call(false, imagePath);

                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void takePhoto() {
        isTakePhoto = true;
        //调用相机拍照
        mCamera.takePicture(null, null, null, (data, camera1) -> {
            //视图动画
            mPhotoLayout.setVisibility(View.GONE);
            mConfirmLayout.setVisibility(View.VISIBLE);
            imageData = data;
            //停止预览
            mCamera.stopPreview();
        });
    }

    public interface CameraCall {
        void call(boolean flag, String path);
    }
}
