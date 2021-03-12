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
import android.provider.OpenableColumns;
import android.text.TextUtils;

import com.zyq.permission.OnPermission;
import com.zyq.permission.Permission;
import com.zyq.permission.QPermissions;
import com.zyq.ui.camare.CameraActivity;
import com.zyq.ui.camare.CameraView;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.lang.RuntimeException;
import java.util.List;

import static com.zyq.SuperCompression.FileUtils.UriToFile;


public class QPhotoUtils {

    public static String authorities = "";

    private Executer executer;

    public interface Callback {
        void let(Uri uri, boolean result, String arg);
    }

    public static class PhotoFragment extends Fragment {
        private final int REQUEST_CODE_CROP = 601;
        private final int REQUEST_CODE_CAMERA = 602;
        private final int REQUEST_CODE_SELECT = 603;
        private Callback cropCallback = null;
        private Callback selectCallback = null;
        private Callback cameraCallback = null;

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

        String cameraPath;

        //调用相机
        void camera(Callback callback) {
            if (TextUtils.isEmpty(authorities))
                throw new RuntimeException("authority不能为空");

            this.cameraCallback = callback;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            File file = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() +
                    System.currentTimeMillis() + ".jpg");
            cameraPath = file.getAbsolutePath();
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(requireContext(), authorities, file);
            } else {
                uri = Uri.fromFile(file);
            }
//            if(!JumpAuthorities){
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//            }
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }

        //选择图片
        void select(Callback callback) {
            this.selectCallback = callback;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_SELECT);
        }

        Uri outUri = null;

        //裁剪

        void crop(Uri uri, int aspectX, int aspectY, int outputX, int outputY, Callback callBack) {
            if (TextUtils.isEmpty(authorities))
                throw new RuntimeException("请填写正确的authority");
            Uri uri1 = null;
            if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
                uri1 = FileProvider.getUriForFile(requireContext(), authorities, UriToFile(requireContext(), uri));
            } else {
                uri1 = uri;
            }
//            Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null, null);
            Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                this.cropCallback = callBack;
                Intent intent = new Intent("com.android.camera.action.CROP");
                //文件名
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
                outUri = Uri.fromFile(new File(requireContext().getExternalCacheDir().getAbsolutePath() + System.currentTimeMillis() + displayName));
                requireContext().grantUriPermission(
                        requireContext().getPackageName(),
                        outUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("noFaceDetection", true); //去除默认的人脸识别，否则和剪裁匡重叠
                intent.setDataAndType(uri1, requireContext().getContentResolver().getType(uri));
                intent.putExtra("crop", "true"); // crop=true 有这句才能出来最后的裁剪页面.
                intent.putExtra("output", outUri);
                intent.putExtra("outputFormat", "JPEG"); // 返回格式
                int ax = aspectX;
                int ay = aspectY;
                if (ay != 0 && ay != 0) {
                    if (ax == ay && Build.MANUFACTURER == "HUAWEI") {
                        ax = 9998;
                        ay = 9999;
                    }
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

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {

                switch (requestCode) {
                    case REQUEST_CODE_CROP:
                        //裁剪
                        if (cropCallback != null) {
                            cropCallback.let(outUri, true, "");
                        }
                        cropCallback = null;
                        break;
                    case REQUEST_CODE_CAMERA:
                        Uri uri = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(requireContext(),
                                    authorities, new File(cameraPath));
                        } else {
                            uri = Uri.fromFile(new File(cameraPath));
                        }
                        if (cameraCallback != null) {
                            cameraCallback.let(uri, true, "");
                        }
                        cameraCallback = null;
                        break;
                    case REQUEST_CODE_SELECT:
                        Uri uri2 = data.getData();
                        if (selectCallback != null) {
                            selectCallback.let(uri2, true, "");
                        }
                        selectCallback = null;
                        break;

                }


            } else {
                switch (requestCode) {
                    case REQUEST_CODE_CROP:
                        //裁剪
                        cropCallback.let(null, false, "裁剪失败");
                        cropCallback = null;
                        break;
                    case REQUEST_CODE_CAMERA:
                        cameraCallback.let(null, false, "拍照失败");
                        cameraCallback = null;
                        break;

                    case REQUEST_CODE_SELECT:
                        selectCallback.let(null, false, "选择图片失败");
                        selectCallback = null;
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

    public static void select(FragmentManager manager, Callback photoCallBack) {
        getPhotoFragment(manager).select(photoCallBack);
    }


    /**
     * 打开相册
     */

    public static void select(FragmentActivity activity, Callback photoCallBack) {

        getPhotoFragment(activity.getSupportFragmentManager()).select(photoCallBack);
    }

    /**
     * 打开相册
     */

    public static void select(Fragment fragment, Callback photoCallBack) {
        getPhotoFragment(fragment.getChildFragmentManager()).select(photoCallBack);
    }

    /**
     * 调用相机拍照
     */
    static void camera(FragmentManager manager, Callback photoCallBack) {
        getPhotoFragment(manager).camera(photoCallBack);
    }

    /**
     * 调用相机拍照
     */

    public static void camera(FragmentActivity activity, Callback photoCallBack) {
        getPhotoFragment(activity.getSupportFragmentManager()).
                camera(photoCallBack);
    }

    public static void cameraCard(Context activity, CameraView.CameraCall cameraCall) {
        CameraActivity.startCamera(activity, cameraCall);
    }

    /**
     * 调用相机拍照
     */
    public static void camera(Fragment fragment, Callback photoCallBack) {
        getPhotoFragment(fragment.getChildFragmentManager()).
                camera(photoCallBack);
    }

    private static void crop(FragmentManager manager, Uri uri, int aspectX, int aspectY, int outputX, int outputY, Callback cropCallBack) {
        getPhotoFragment(manager).
                crop(uri, aspectX, aspectY, outputX, outputY, cropCallBack);
    }

    /**
     * 调用裁剪功能
     */
    public static Crop crop(FragmentManager manager) {
        return new Crop(manager);
    }

    /**
     * 调用裁剪功能
     */
    public static Crop crop(FragmentActivity activity) {
        return crop(activity.getSupportFragmentManager());
    }


    public static class Crop {
        FragmentManager manager;

        Crop(FragmentManager manager) {
            this.manager = manager;
        }

        private int aspectX = 0;
        private int aspectY = 0;
        private int outputX = 0;
        private int outputY = 0;

        /**
         * 设置比例
         */
        Crop setAspect(int aspectX, int aspectY) {
            this.aspectX = aspectX;
            this.aspectY = aspectY;
            return this;
        }

        /**
         * 设置输出图片的宽高
         */
        Crop setOutput(int width, int height) {
            this.outputX = width;
            this.outputY = height;
            return this;
        }

        void build(Uri uri, Callback cropCallBack) {
            crop(manager, uri, aspectX, aspectY, outputX, outputY, cropCallBack);
        }
    }
    interface Executer {
        void exe(Callback callback);
    }
}