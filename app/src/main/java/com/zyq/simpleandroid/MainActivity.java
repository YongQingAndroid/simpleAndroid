package com.zyq.simpleandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.jfz.wealth.R;
import com.zyq.simplestore.SimpleStore;
import com.zyq.simplestore.core.DbOrmHelper;
import com.zyq.simplestore.imp.DbTableName;;
import com.zyq.simplestore.imp.DbToMany;
import com.zyq.simplestore.imp.DbToOne;
import com.zyq.simplestore.log.LightLog;

import java.util.ArrayList;
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
        List<TestOrm> result = DbOrmHelper.getInstent().query(TestOrm.class);
        LightLog.I(result.get(0).test.name);

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

        @DbToMany
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
