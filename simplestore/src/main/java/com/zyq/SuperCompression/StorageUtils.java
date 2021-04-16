package com.zyq.SuperCompression;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 存储相关的工具类
 * <p>
 * reference UIL#StorageUtils
 *
 * @author zhangchaoxian@jinfuzi.com
 * @see Environment
 * @see android.os.StatFs
 */
public class StorageUtils {

    /**
     * Images folder, like Picture field
     */
    private static final String DIR_IMAGE = "Images";

    /**
     * Document folder, compat field Environment#DIRECTORY_DOCUMENTS
     */
    private static final String DIRECTORY_DOCUMENTS = "Documents";

    /**
     * Log folder
     */
    private static final String DIRECTORY_LOG = "Log";

    /**
     * Template folder
     */
    private static final String DIR_TMP = "tmp";

    private StorageUtils() {
    }

    /**
     * App 内部或者外部的应用files目录，优先使用外部存储位置。
     * <p>
     * 1./sdcard/Android/data/pkg/files  /storage/emulated/0/Android/data/pkg/files
     * 2./data/data/pkg/files
     */
    public static File getAppFilesDir(Context context) {
        File filesDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            filesDir = context.getExternalFilesDir(null);
        }
        if (filesDir == null || !filesDir.exists()) {
            filesDir = context.getFilesDir();
        }
        return ensureDirs(filesDir);
    }

    /**
     * data/files目录下的指定目录
     * <p>
     * 1. /sdcard/Android/data/pkg/files/subDir
     * 2. /data/data/pkg/files/dir
     */
    public static File getAppFilesDir(Context context, String subDir) {
        File dir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = context.getExternalFilesDir(subDir);
        }
        if (dir == null || !dir.exists()) {
            dir = new File(context.getFilesDir(), subDir);
        }
        return ensureDirs(dir);
    }

    private static File ensureDirs(File dir) {
        if (dir != null && !dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Log 文件目录，files/log
     */
    public static File getAppLogDir(Context context) {
        return getAppFilesDir(context, DIRECTORY_LOG);
    }

    /**
     * 图片目录, files/Images
     */
    public static File getAppImagesDir(Context context) {
        return getAppFilesDir(context, DIR_IMAGE);
    }

    /**
     * 文档目录， files/Document
     */
    public static File getAppDocDir(Context context) {
        return context.getExternalFilesDir(DIRECTORY_DOCUMENTS);
    }

    /**
     * 缓存目录： data/pkg/cache
     */
    public static File getAppCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * 临时目录：data/pkg/tmp
     */
    public static File getAppTmpDir(Context context) {
//        return context.getDir(DIR_TMP, Context.MODE_WORLD_WRITEABLE);
        return context.getDir(DIR_TMP, Context.MODE_PRIVATE);

    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    private static final String TAG = "StorageUtils";
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

}
