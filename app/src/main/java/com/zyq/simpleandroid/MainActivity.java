package com.zyq.simpleandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.jfz.wealth.R;
import com.zyq.SuperCompression.QCompression;
import com.zyq.simplestore.core.DbOrmHelper;
import com.zyq.handler.WorkHandler;
import com.zyq.simplestore.imp.DbTableName;;
import com.zyq.simplestore.imp.DbToOne;
import com.zyq.simplestore.log.LightLog;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void mClick(View view) {
//        SimpleStore.store("name", "5656565656");
//        SimpleStore.openMmap();

//        LightLog.I(SimpleStore.praseKey("name").get(String.class));
        TestOrm testOrm = new TestOrm("1", "lisa");
        TestOrm1 testOrm1 = new TestOrm1("1", "tom6668886");
//        TestOrm1 testOrm2 = new TestOrm1("1", "testLisa");
        testOrm.test = testOrm1;
        DbOrmHelper.getInstent().save(testOrm);
//        List<TestOrm> result = DbOrmHelper.getInstent().query(TestOrm.class);
//        LightLog.I(result.get(0).test.name);

        WorkHandler.from(DbOrmHelper.getInstent())
                .executeOn(WorkHandler.schedulerWorkThread())
                .map(dbOrmHelper -> dbOrmHelper.query(TestOrm.class))
                .map(obj -> {
                    LightLog.i("从数据库查询回调中切片取出" + obj.get(0).test.name);
                    return "搜索到了" + obj.size() + "个数据";
                })
                .executeOn(WorkHandler.schedulerAndroidMainThread())
                .setResult(new WorkHandler.ResultCallBack<String>() {
                    @Override
                    public void onSuccess(String obj) {
                        LightLog.i("我是回调：" + obj);
                    }

                    @Override
                    public void onError(Exception e) {
                        LightLog.i("错误信息：" + e.getMessage());
                    }
                });
        QCompression.newInstance()
                .getCompressionBuilder(null)
                .from("")
                .setHeight(100)
                .setWidth(100)
                .setMaxSize(100)
                .get(new QCompression.CompressionCallback() {
                    @Override
                    public void onStart(Context context) {

                    }

                    @Override
                    public void onSuccess(List<File> files) {

                    }

                    @Override
                    public void onErr(Exception e) {

                    }
                });
//        LightLog.i(JSON.toJSONString(testOrm));
    }


    public void mClicktime(View view) {
//        TimePickerManager.getInstance().showPicker(this, FormatState.YYYY, FormatState.MM,FormatState.DD,FormatState.HH,FormatState.mm.setJump(30));
//       new SqliteDataSource1().execute();

    }

    public void mClickcity(View view) {

//        new MeterialCityDialog(this).show();
    }

    @DbTableName("TestOrm")
    public static class TestOrm {
        public String id;
        public String name;

        public TestOrm() {
        }

        public TestOrm(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @DbToOne
        TestOrm1 test;

    }

    public static class TestOrm1 {
        public String id;
        public String name;

        public TestOrm1(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
