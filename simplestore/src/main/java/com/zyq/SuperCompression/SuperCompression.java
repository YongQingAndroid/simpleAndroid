package com.zyq.SuperCompression;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 简单压缩框架
 */
public class SuperCompression {

    CompressionBuilder compressionBuilder;
    CompressionCallback callback;

    public static SuperCompression newInstance() {
        return new SuperCompression();
    }

    public static CompressionBuilder newBuilderInstance(Context context) {
        return new CompressionBuilder(context);
    }

    static List<File> newInstanceToGet(CompressionBuilder compressionBuilder, CompressionCallback callback) {
        return new SuperCompression(compressionBuilder).get(callback);
    }

    public CompressionBuilder getCompressionBuilder(Context context) {
        if (compressionBuilder == null)
            compressionBuilder = new CompressionBuilder(context);
        return compressionBuilder;
    }

    /**
     * @param compressionBuilder
     * @return
     */
    public CompressionBuilder setCompressionBuilder(CompressionBuilder compressionBuilder) {
        CompressionBuilder clone = new CompressionBuilder(compressionBuilder.context);
        clone.uri = compressionBuilder.uri;
        clone.maxSize = compressionBuilder.maxSize;
        clone.width = compressionBuilder.width;
        clone.height = compressionBuilder.height;
        clone.compressionType = compressionBuilder.compressionType;
        clone.outFile = compressionBuilder.outFile;
        this.compressionBuilder = clone;
        return clone;
    }

    SuperCompression() {

    }

    /***
     *
     * @return
     */
    public List<File> get() {
        return get(null);
    }

    /**
     * 开始压缩
     *
     * @param callback
     * @return
     */
    public List<File> get(CompressionCallback callback) {
        this.callback = callback;
        if (callback == null) {
            try {
                return sizeCompress(compressionBuilder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            callback.onStart(compressionBuilder.context);
            execute(() -> {
                try {
                    List<File> result = sizeCompress(compressionBuilder);
                    new Handler(compressionBuilder.context.getMainLooper()).post(() -> {
                        callback.onSuccess(result);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onErr(e);
                }
            });
        }
        return null;
    }

    /**
     * @param compressionBuilder
     * @return
     * @throws Exception
     */
    //尺寸压缩
    public static List<File> sizeCompress(CompressionBuilder compressionBuilder) throws Exception {//radio:5=47.85kb,2=221.39kb

        Bitmap bitmap = getBitmapFormUri(compressionBuilder);
        if (bitmap == null) {
            return null;
        }
        File file = saveBimap(bitmap, getOutFile(compressionBuilder));
        List<File> files = new ArrayList<>();
        files.add(file);
        return files;

    }

    private static File getOutFile(CompressionBuilder compressionBuilder) {
        return compressionBuilder.getOutFile();
    }

    /***
     * 获取压缩宽高后的bitmap
     * @param compressionBuilder
     * @return
     * @throws IOException
     */
    public static Bitmap getBitmapFormUri(CompressionBuilder compressionBuilder) throws IOException {
        Context ac = compressionBuilder.context;
        Uri uri = compressionBuilder.uri;
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);


        float hh = compressionBuilder.height;
        float ww = compressionBuilder.width;

        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

        bitmapOptions.inSampleSize = calculateInSampleSize(onlyBoundsOptions, ww, hh);//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        bitmapOptions.inJustDecodeBounds = false;

        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap, compressionBuilder.getMaxSize());//再进行质量压缩
    }

    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int maxSize) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > maxSize) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差 ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        image.recycle();
        return bitmap;
    }

    /**
     * @param bitmap
     * @param file
     * @return
     */
    public static File saveBimap(Bitmap bitmap, File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(file);

            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;

    }

    private SuperCompression(CompressionBuilder compressionBuilder) {
        this.compressionBuilder = compressionBuilder;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, float reqWidth, float reqHeight) {
        // Raw height and width of image
        if (reqHeight == 0) {
            reqHeight = options.outHeight / 2;
        }
        if (reqWidth == 0) {
            reqWidth = options.outWidth / 2;
        }
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 压缩构造器
     */
    public static class CompressionBuilder {
        public Context context;
        private int height = 0, width = 0, maxSize = 100;//
        protected CompressionType compressionType;
        Uri uri;
        File outFile;

        CompressionBuilder(Context context) {
            this.context = context;
        }


        private File getOutFile() {
            if (outFile == null && context != null) {
                outFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath() + System.currentTimeMillis() + ".jpg");
            }
            return outFile;
        }

        public void setOutFile(File outFile) {
            this.outFile = outFile;
        }

        public CompressionBuilder from(Uri uri) {
            this.uri = uri;
            return this;
        }

        public CompressionBuilder from(String path) {
            this.uri = Uri.parse(path);
            return this;
        }

        public CompressionBuilder from(File file) {
            this.uri = Uri.fromFile(file);
            return this;
        }

        public int getMaxSize() {
            return maxSize;
        }

        /**
         * @param maxSize 单位K
         * @return
         */
        public CompressionBuilder setMaxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public CompressionBuilder setCompressionType(CompressionType type) {
            return this;
        }

        public Context getContext() {
            return context;
        }

        public CompressionBuilder setContext(Context context) {
            this.context = context;
            return this;
        }

        public int getHeight() {
            return height;
        }

        public CompressionBuilder setHeight(int height) {
            this.height = height;
            return this;
        }

        public int getWidth() {
            return width;
        }

        public CompressionBuilder setWidth(int width) {
            this.width = width;
            return this;
        }

        public CompressionType getCompressionType() {
            return compressionType;
        }

        public List<File> get() {
            return get(null);
        }

        public List<File> get(CompressionCallback compressionCallback) {
            return SuperCompression.newInstanceToGet(this, compressionCallback);
        }
    }

    public enum CompressionType {
        MASS,//质量压缩
        SIZE,//尺寸压缩
        AUTO//自动压缩
    }

    public interface CompressionCallback {
        void onStart(Context context);

        void onSuccess(List<File> files);

        void onErr(Exception e);
    }

    static ExecutorService fixedThreadPool = Executors.newSingleThreadExecutor();

    public static void execute(Runnable runnable) {
        fixedThreadPool.execute(runnable);
    }


}
