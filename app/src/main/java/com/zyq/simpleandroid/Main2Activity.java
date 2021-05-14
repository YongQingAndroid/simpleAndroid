package com.zyq.simpleandroid;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;
import com.jfz.wealth.R;
import com.zyq.SuperCompression.QCompression;
import com.zyq.SuperCompression.QPhotoUtils;
import com.zyq.permission.OnPermission;
import com.zyq.permission.Permission;
import com.zyq.permission.QPermissions;
import com.zyq.ui.camare.CameraView;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;


public class Main2Activity extends BaseActivity {
    CameraView cameraView = null;
    static Main2Activity self;
    LinearLayout group;
    ImageView img1;
    MarqueeGroup mMarqueeGroup;
    RequestManager requestManager;
    Uri path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self=this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main2);
        Gson.setStrictMode(false);
        img1 = findViewById(R.id.img1);
        requestManager=Glide.with(getApplicationContext());
        if(savedInstanceState!=null){
            path=savedInstanceState.getParcelable("data");
        }
        if(path!=null){
            requestManager.load(path).into(img1);
        }
//        cameraView = findViewById(R.id.camera);
//        cameraView.setCameraCall((flag, path) -> {
//            if (flag) {
//                Glide.with(Main2Activity.this).load(path).into(img1);
//            }
//        });
    }

    public void pause(View view){
        if(mMarqueeGroup!=null){
            mMarqueeGroup.pause();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void goLogin(View view) {
//        mMarqueeGroup = findViewById(R.id.MarqueeGroup);
//        new MarqueeGroup.Builder().setAdapter(new Adapter()).setShowLine(1).setCatchSize(5).setRecycle(false).bindView(mMarqueeGroup);
////        new MarqueeGroup.Builder().setShowLine(3).bindView(mMarqueeGroup);
//        mMarqueeGroup.start();
//        StatusBarCompat.barTransparent(this);
        QPhotoUtils.authorities = "com.jfz.wealth.fileprovider";
//        QPhotoUtils.cameraCard(this, (flag, path) -> {
//            if (flag) {
//                Glide.with(Main2Activity.this).load(path).into(img1);
//            }
//        });
//        startActivity(new Intent(this, CameraActivity.class));
////        AutoLoginManager.getInstance().goLogin(this);
//        CameraActivity.startMe(this, 2005, CameraActivity.MongolianLayerType.IDCARD_POSITIVE);

//        QPhotoUtils.authorities = "com.jfz.wealth.fileprovider";
//
        QPermissions.with(this).permission(Permission.CAMERA, Permission.MANAGE_EXTERNAL_STORAGE).request(new OnPermission() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                if (all) {
                   new QPhotoUtils.Builder().bind(Main2Activity.this).cameraCard((flag, path) -> {
                       ImageView img1 = findViewById(R.id.img1);
                                        Glide.with(Main2Activity.this).load(path).into(img1);
                   });

//                    QPhotoUtils.camera(Main2Activity.this, (uri, result, arg) -> {
//                        QCompression.newInstance()
//                                .getCompressionBuilder(Main2Activity.this)
//                                .from(uri)
//                                .setMaxSize(100)
//                                .get(new QCompression.CompressionCallback() {
//                                    @Override
//                                    public void onStart(Context context) {
//
//                                    }
//
//                                    @Override
//                                    public void onSuccess(List<File> files) {
//                                        File file = files.get(0);
//                                        ImageView img1 = findViewById(R.id.img1);
//                                        Glide.with(Main2Activity.this).load(file).into(img1);
//                                        Toast.makeText(Main2Activity.this, "" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                                    }
//
//                                    @Override
//                                    public void onErr(Exception e) {
//
//                                    }
//                                });
//
//                    });
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean never) {

            }
        });


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("data",path);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        self=null;
        super.onDestroy();

    }

    public void getToken(View view) {
//        AutoLoginManager.getInstance().getToken(this);
    }

    static class Adapter extends MarqueeGroup.MarqueeAdapter<Holder> {
        @Override
        public int getItemCount() {
            return 5;
        }

        @Override
        public Holder createViewHolder(ViewGroup viewGroup, int type) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
            return new Holder(view);
        }

        @Override
        public void bindViewHolder(Holder holder, int position) {
            int[] colors = new int[]{Color.BLUE, Color.RED};
            holder.textView.setBackgroundColor(colors[position % 2]);
//            if(position%2==0){
//                holder.textView.setTextSize(30);
//            }else {
//                holder.textView.setTextSize(15);
//            }
            holder.textView.setText("----item------" + position);
//            holder.textView.post(new Runnable() {
//                @Override
//                public void run() {
//                    holder.textView.setText("----item------" + position);
//                }
//            });

        }
    }

    static class Holder extends MarqueeGroup.MarqueeHolder {
        TextView textView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_name);
            textView.setBackgroundColor(Color.YELLOW);
            itemView.setOnClickListener(view -> {
//                Toast.makeText(view.getContext(), "position"+getAdapterPosition(), Toast.LENGTH_SHORT).show();
            });
        }
    }

}
