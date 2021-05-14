package com.zyq.ui.camare;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    private LinearLayout mConfirmLayout;
    private RelativeLayout mPhotoLayout;
    private View maskView, tackPhotoView;//遮罩
    private CameraDrawable cameraDrawable;
    private boolean isCard=false;
    private boolean isTakePhoto;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
//    SensorControler sensorControler;
    private CameraCall cameraCall;
    int mScreenWidth,mScreenHeight;
    private int[] state_press = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};

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

    public void setCard(boolean card) {
        isCard = card;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public CameraView(Context context) {
        super(context);
        initUi();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initUi() {
        DisplayMetrics mDisplayMetrics = getContext().getResources()
                .getDisplayMetrics();
        mScreenWidth = mDisplayMetrics.widthPixels;
        mScreenHeight = mDisplayMetrics.heightPixels;
        sensorControler=SensorControler.getInstance(getContext());
        sensorControler.setCameraFocusListener(() -> {
            makeFocus(mScreenWidth/2,mScreenHeight/2);
        });


        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, sp2px(80));
        lp.gravity = Gravity.BOTTOM;

        mPhotoLayout = new RelativeLayout(getContext());
        mPhotoLayout.setGravity(Gravity.CENTER_VERTICAL);
        mPhotoLayout.setLayoutParams(lp);

        mPhotoLayout.setPadding(0, sp2px(10), 0, sp2px(10));
        tackPhotoView = new View(getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(sp2px(60), sp2px(60));
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        tackPhotoView.setOnClickListener(this);
        StateListDrawable btnDrawable = new StateListDrawable();
        btnDrawable.addState(state_press, new ButtonDrawable(getContext(), Color.YELLOW));
        btnDrawable.addState(new int[]{}, new ButtonDrawable(getContext()));


        tackPhotoView.setBackground(btnDrawable);
        mPhotoLayout.addView(tackPhotoView, layoutParams);
        mPhotoLayout.setBackgroundColor(Color.BLACK);


        TextView textView = new TextView(getContext());
        textView.setText("取消");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams1.rightMargin=sp2px(10);
        mPhotoLayout.addView(textView, layoutParams1);
        textView.setOnClickListener(view -> {
            if (cameraCall != null)
                cameraCall.call(false, null);
        });


        LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mConfirmLayout = new LinearLayout(getContext());
        mConfirmLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        mConfirmLayout.setLayoutParams(lp2);
        mConfirmLayout.setOrientation(LinearLayout.HORIZONTAL);
        mConfirmLayout.setVisibility(View.GONE);
        mConfirmLayout.setBackground(new CameraDrawable(getContext(), 255));
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

    private int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getContext().getResources().getDisplayMetrics());
    }

    private void cancleSavePhoto() {
        mPhotoLayout.setVisibility(View.VISIBLE);
        mConfirmLayout.setVisibility(View.GONE);
        //开始预览
        startCamare();
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
            maskView.setVisibility(isCard?VISIBLE:GONE);
            maskView.setBackground(cameraDrawable);
            mOverCameraView = new OverCameraView(getContext());
            this.addView(cameraPreview);
            this.addView(mOverCameraView);
            this.addView(maskView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            this.addView(mPhotoLayout);
            this.addView(mConfirmLayout);
            sensorControler.onStart();

        } else {
            startCamare();
        }
    }

    private void startCamare() {
        mCamera.startPreview();
        sensorControler.onStart();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCamera == null)
            super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            makeFocus(x,y);
        }
        return super.onTouchEvent(event);
    }

    private void makeFocus(float x,float y) {
        if (!isFoucing) {

            isFoucing = true;
            if (mCamera != null && !isTakePhoto) {
                mOverCameraView.setTouchFoucusRect(mCamera, autoFocusCallback, x, y);
            }
            mRunnable = () -> {
                Toast.makeText(getContext(), "自动聚焦超时,请调整合适的位置拍摄！", Toast.LENGTH_SHORT);
                isFoucing = false;
                mOverCameraView.setFoucuing(false);
                mOverCameraView.disDrawTouchFocusRect();
                sensorControler.locked=false;

            };
            //设置聚焦超时
            mHandler.postDelayed(mRunnable, 3000);
        }
    }


    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            isFoucing = false;
            mOverCameraView.setFoucuing(false);
            mOverCameraView.disDrawTouchFocusRect();
            //停止聚焦超时回调
            mHandler.removeCallbacks(mRunnable);
            sensorControler.locked=false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        takePhoto();
    }

    private void savePhoto() {
        FileOutputStream fos = null;
        String cameraPath = getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera";
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
                    if(isCard){
                        BitmapUtils.saveBitmap(BitmapUtils.cropCardBitmap(retBitmap), imagePath);
                    }else {
                        BitmapUtils.saveBitmap(retBitmap, imagePath);
                    }
                    if (cameraCall != null)
                        cameraCall.call(true, imagePath);
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
            if(!isCard){
                mConfirmLayout.setBackgroundColor(Color.TRANSPARENT);
            }
            imageData = data;
            //停止预览
            stopCamera();
        });
    }

    private void stopCamera() {
        mCamera.stopPreview();
        sensorControler.onStop();
    }

    public interface CameraCall {
        void call(boolean flag, String path);
    }
}
