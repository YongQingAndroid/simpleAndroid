package com.zyq.SuperCompression;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.zyq.permission.OnPermission;
import com.zyq.permission.Permission;
import com.zyq.permission.QPermissions;
import com.zyq.ui.camare.CameraActivity;
import com.zyq.ui.camare.CameraView;

import java.io.File;
import java.lang.RuntimeException;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import static com.zyq.SuperCompression.FileUtils.UriToFile;

public class QPhotoUtils {

    public static String authorities = "";
    public interface Callback {
        void let(Uri uri, boolean result, String arg);
    }
    public static class Builder{
        private Crop crop;
        private FragmentManager fragmentManager;
        private Context context;
        public Builder bind(FragmentActivity fragmentActivity){
            this.fragmentManager= fragmentActivity.getSupportFragmentManager();
            this.context=fragmentActivity;
            return this;
        }
        public Builder bind(Fragment fragment){
            this.fragmentManager= fragment.getChildFragmentManager();
            this.context=fragment.getActivity();
            return this;
        }

        public Builder setCrop(Crop crop) {
            this.crop = crop;
            return this;
        }

        public  void select(Callback photoCallBack) {
            getPhotoFragment(this.fragmentManager).setBuilder(this).select(photoCallBack);
        }
        public  void camera( Callback photoCallBack) {
            getPhotoFragment(this.fragmentManager).setBuilder(this).camera(photoCallBack);
        }

        public  void cameraCard(CameraView.CameraCall cameraCall) {
            CameraActivity.startCamera(context, cameraCall);
        }
    }
    public static class PhotoFragment extends Fragment {
        private final int REQUEST_CODE_CROP = 601;
        private final int REQUEST_CODE_CAMERA = 602;
        private final int REQUEST_CODE_SELECT = 603;
        private Callback mCallback = null;
        String cameraPath;
        Builder builder;
        File cropFile;
        Uri outUri = null;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public void onResume() {
            super.onResume();
            QPermissions.with(getActivity()).permission(Permission.CAMERA, Permission.MANAGE_EXTERNAL_STORAGE).request(new OnPermission() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void hasPermission(List<String> granted, boolean all) {
                    if (all) {

                    }
                }

                @Override
                public void noPermission(List<String> denied, boolean never) {

                }
            });
        }



        public PhotoFragment setBuilder(Builder builder) {
            this.builder = builder;
            return this;
        }

        //调用相机
        void camera(Callback callback) {
            if (TextUtils.isEmpty(authorities))
                throw new RuntimeException("authority不能为空");

            this.mCallback = callback;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            File file =null;
            file=generateOutput(getActivity());

            cameraPath = file.getAbsolutePath();
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(requireContext(), authorities, file);
            }  else {
                uri = Uri.fromFile(file);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }

        //选择图片
        void select(Callback callback) {
            this.mCallback = callback;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_SELECT);
        }


        //裁剪
        void crop(Crop mCrop,Uri uri){
            crop(uri, mCrop.aspectX, mCrop.aspectY, mCrop.outputX, mCrop.outputY, mCallback);
        }
        private File generateOutput(Context activity) {
            File dir = StorageUtils.getAppImagesDir(activity);
            File file = FileUtils.createJpegImage(dir, "pick-tmp-");
            return file;
        }
        void crop(Uri uri, int aspectX, int aspectY, int outputX, int outputY, Callback callBack) {

            if (TextUtils.isEmpty(authorities))
                throw new RuntimeException("请填写正确的authority");
            Uri uri1 = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q&&uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
                uri1 = FileProvider.getUriForFile(requireContext(), authorities, UriToFile(requireContext(), uri));
            } else {
                uri1 = uri;
            }
            this.mCallback = callBack;
            Intent intent = new Intent("com.android.camera.action.CROP");
            //文件名
            String displayName =System.currentTimeMillis()+"crop.jpg";
            if (Build.VERSION.SDK_INT >= 30) {
                //android 11以上，将文件创建在公有目录
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
                //storage/emulated/0/Pictures
                cropFile = new File(path, System.currentTimeMillis()   + displayName);
                outUri= Uri.parse("file://" + cropFile.getAbsolutePath());
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                cropFile = new File(requireContext().getExternalCacheDir().getAbsolutePath() + System.currentTimeMillis() + displayName);
                outUri= Uri.parse("file://" + cropFile.getAbsolutePath());
            }else {
                cropFile=generateOutput(getActivity());
                outUri=Uri.fromFile(cropFile);
            }
            requireContext().grantUriPermission(
                    requireContext().getPackageName(),
                    outUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("noFaceDetection", true); //去除默认的人脸识别，否则和剪裁匡重叠
            intent.setDataAndType(uri1, "image/*");
            intent.putExtra("crop", "true"); // crop=true 有这句才能出来最后的裁剪页面.
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
            intent.putExtra("outputFormat", "JPEG"); // 返回格式
            int ax = aspectX;
            int ay = aspectY;
            if (ay != 0 && ay != 0) {
//
                intent.putExtra("aspectX", ax); // 这两项为裁剪框的比例.
                intent.putExtra("aspectY", ay);// x:y=1:2
            }
            if (outputX != 0 && outputY != 0) {
                intent.putExtra("outputX", outputX);
                intent.putExtra("outputY", outputY);
            }
            intent.putExtra("return-data", false);
            startActivityForResult(
                    intent, REQUEST_CODE_CROP
            );

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {

                switch (requestCode) {
                    case REQUEST_CODE_CROP:
                        //裁剪
                        if (mCallback != null) {
                            mCallback.let(outUri, true, cropFile.getAbsolutePath());
                        }
                        mCallback = null;
                        break;
                    case REQUEST_CODE_CAMERA:
                        Uri uri = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            uri = FileProvider.getUriForFile(requireContext(),
                                    authorities, new File(cameraPath));
                        } else {
                            uri = Uri.fromFile(new File(cameraPath));
                        }
                        if(builder.crop!=null){
                            crop(builder.crop,uri);
                            return;
                        }
                        if (mCallback != null) {
                            mCallback.let(uri, true, cameraPath);
                        }
                        mCallback = null;
                        break;
                    case REQUEST_CODE_SELECT:
                        Uri uri2 = data.getData();
                        if(builder.crop!=null){
                            crop(builder.crop,uri2);
                            return;
                        }
                        if (mCallback != null) {
                            String path=FileUtils.getFileAbsolutePath(getActivity(),uri2);
                            mCallback.let(uri2, true, path);
                        }
                        mCallback = null;
                        break;

                }


            } else {
                switch (requestCode) {
                    case REQUEST_CODE_CROP:
                        //裁剪
                        mCallback.let(null, false, "裁剪失败");
                        mCallback = null;
                        break;
                    case REQUEST_CODE_CAMERA:
                        mCallback.let(null, false, "拍照失败");
                        mCallback = null;
                        break;

                    case REQUEST_CODE_SELECT:
                        mCallback.let(null, false, "选择图片失败");
                        mCallback = null;
                        break;
                }
            }
        }
    }

    private static QPhotoUtils.PhotoFragment getPhotoFragment(FragmentManager manager) {
        QPhotoUtils.PhotoFragment photoFragment = (QPhotoUtils.PhotoFragment) manager.findFragmentByTag("photoFragmen");
        if (photoFragment == null) {
            photoFragment = new QPhotoUtils.PhotoFragment();
        } else {
            return photoFragment;
        }
        manager.beginTransaction()
                .add(photoFragment, "photoFragmen")
                .commitAllowingStateLoss();
        manager.executePendingTransactions();
        return photoFragment;
    }


    /**
     * 打开相册
     */

    public static void select(Builder builder, Callback photoCallBack) {
        getPhotoFragment(builder.fragmentManager).setBuilder(builder).select(photoCallBack);
    }
    public static void camera(Builder builder, Callback photoCallBack) {
        getPhotoFragment(builder.fragmentManager).setBuilder(builder).camera(photoCallBack);
    }

    private static void crop(FragmentManager manager, Uri uri, int aspectX, int aspectY, int outputX, int outputY, Callback mCallback) {
        getPhotoFragment(manager).
                crop(uri, aspectX, aspectY, outputX, outputY, mCallback);
    }



    public static class Crop {
        FragmentManager manager;
        public static  Crop create(){
            return new Crop();
        }
        Crop() {
        }
        Crop(FragmentManager manager) {
            this.manager = manager;
        }

        private int aspectX = 0;
        private int aspectY = 0;
        private int outputX = 0;
        private int outputY = 0;
        private Uri uri;
        /**
         * 设置比例
         */
        public Crop setAspect(int aspectX, int aspectY) {
            this.aspectX = aspectX;
            this.aspectY = aspectY;
            return this;
        }

        /**
         * 设置输出图片的宽高
         */
        public Crop setOutput(int width, int height) {
            this.outputX = width;
            this.outputY = height;
            return this;
        }

        public Crop setUri(Uri uri) {
            this.uri = uri;
            return this;
        }

        void build(Callback mCallback) {
            crop(manager, uri, aspectX, aspectY, outputX, outputY, mCallback);
        }
    }
}