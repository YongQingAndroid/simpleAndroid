package com.zyq.simplecitypicker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.alibaba.fastjson.JSON;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * package kotlinTest:com.qing.lightview.material.CityDataSource.class
 * 作者：zyq on 2017/7/31 15:41
 * 邮箱：zyq@posun.com
 */
public class SqliteDataSource1 {
    private String dnname = "simple_city111.db";
    private String filePath = "";
//    public static String configFile="assets/city111.db";
private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
    private SQLiteDatabase openDatabase(Context context) {
        filePath = context.getApplicationContext().getCacheDir().getAbsolutePath() + dnname;
        File jhPath = new File(filePath);
        // 查看数据库文件是否存在
        if (jhPath.exists()) {
//            LightViewLog.i("存在数据库");
            return SQLiteDatabase.openOrCreateDatabase(jhPath, null);
        } else {
            try {
//                InputStream stream =  ClassLoader.getSystemClassLoader().getResourceAsStream(configFile);
                InputStream stream = context.getResources().openRawResource(R.raw.city111);
                FileOutputStream fos = new FileOutputStream(jhPath);
                byte[] buffer = new byte[2048];
                int count = 0;
                while ((count = stream.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fos.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return openDatabase(context);
        }
    }


    public List<MyCityData> getChild(String pid) {
        SQLiteDatabase database = null;
        try {
            database = openDatabase(HookApplication.getApplication());
            Cursor cursor = database.rawQuery("SELECT * FROM crm_china_region WHERE parent_id='" + pid + "'", new String[]{});
            if (cursor == null)
                return null;
            List<MyCityData> arrayList = new ArrayList<>();
            while (cursor.moveToNext()) {
                MyCityData item = new MyCityData();

                item.setName(cursor.getString(cursor.getColumnIndex("name")));
                item.setId(cursor.getString(cursor.getColumnIndex("id")));
                item.setFatherId(cursor.getString(cursor.getColumnIndex("parent_id")));
                item.setLevel(cursor.getString(cursor.getColumnIndex("level")));
                arrayList.add(item);
                Log.i("sql---getChild--", item.getName()+"level=="+ item.level);
            }
            cursor.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null)
                database.close();
        }
        return null;
    }



    public List<MyCityData> getlevel(String leve) {
        SQLiteDatabase database = null;
        try {
            database = openDatabase(HookApplication.getApplication());
            Cursor cursor = database.rawQuery("SELECT * FROM crm_china_region WHERE level='" + leve + "'", new String[]{});
            if (cursor == null)
                return null;
            List<MyCityData> arrayList = new ArrayList<>();
            while (cursor.moveToNext()) {
                MyCityData item = new MyCityData();

                item.setName(cursor.getString(cursor.getColumnIndex("name")));
                item.setId(cursor.getString(cursor.getColumnIndex("id")));
                item.setFatherId(cursor.getString(cursor.getColumnIndex("parent_id")));
                item.setLevel(cursor.getString(cursor.getColumnIndex("level")));
                arrayList.add(item);
                Log.i("sql---Area--", item.getName());
            }
            cursor.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null)
                database.close();
        }
        return null;
    }


    public void execute() {
        List<MyCityData> data1 = getlevel("0");
        dothis(data1);
        Log.i("sql---sucess--","查找数据成功=================");
        try {
//            saveCrashInfo2File(JSON.toJSONString(data1));
            witeXML(data1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("sql---sucess--","写入文件成功=================");
    }
    public String saveCrashInfo2File(String json) {
        StringBuffer sb = new StringBuffer();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            String time = formatter.format(new Date());
            String fileName = "demo-" + time + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory()+"/msm/path/";
                File dir = new File(path);
                if(!dir.exists()){
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(json.getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.i("sql----","写入文件失败=================");
        }
        return null;
    }
    public boolean dothis(List<MyCityData> data) {
        if (Integer.parseInt(data.get(0).level) >= 4) {
            return false;
        }
        for (MyCityData item1 : data) {
            item1.children = getChild(item1.id);
            if(item1.children.size()>0){
                dothis(item1.children);
            }
        }
        return true;
    }
    public void witeXML(List<MyCityData> data ) throws Exception{
        //获取序列化器对象
        Log.i("sql----","开始写入plist=================");
        XmlSerializer serializer = Xml.newSerializer();
        //初始化xml文件
        //file就是你要生成的xml文件
        String time = formatter.format(new Date());
        String path = Environment.getExternalStorageDirectory()+"/msm/path/";
        File file = new File(path+time+"xmltest.xml");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        //参数2：指定xml文件的编码
        serializer.setOutput(fileOutputStream,"utf-8");

        //生成文档声明
        //参数1、用于指定文档声明中的encoding值，并不能决定xml文档使用什么编码
        //参数2、文档是否独立，参见
//        serializer.startDocument("utf-8",true);
        //生成一个开始标签
        //参数1、命名空间，一般不传
        //参数2、标签名称
        serializer.startTag(null,"plist");

        //给开始标签添加属性
        //这个方法必须写在startTag（）之后，而且必须紧跟在startTag（）之后
        //参数1、命名空间
        //参数2、属性名称
        //参数3、属性值
        serializer.attribute(null,"version","1.0");

        //生成一个结束节点
        //参数1、命名空间，一般不传
        //参数2、标签名称
        test(data.get(0).children,"",serializer);
        serializer.endTag(null,"plist");
        //告诉序列化器文件生成完毕
        serializer.endDocument();
        Log.i("sql----","写入plist成功=================");
    }

    public void test(List<MyCityData> data,String pname, XmlSerializer serializer) throws Exception{
        if (Integer.parseInt(data.get(0).level) >= 4) {
            return ;
        }

        serializer.startTag(null,"array");
           for (int i=0;i<data.size();i++){
               MyCityData mMyCityData=data.get(i);
               if(mMyCityData.children.size()>0){
                   serializer.startTag(null,"dict");
                   serializer.startTag(null,"key");
                   serializer.text(mMyCityData.name);
                   serializer.endTag(null,"key");
                   test(mMyCityData.children,mMyCityData.name,serializer);
                   serializer.endTag(null,"dict");
               }else {
                   serializer.startTag(null,"string");
                   serializer.text(mMyCityData.name);
                   serializer.endTag(null,"string");
               }

           }
        serializer.endTag(null,"array");
    }
    static class MyCityData {
        public String name;
        public String fatherId;
        public String id;
        public String level;
        public List<MyCityData> children=new ArrayList<>();

        public String getFatherId() {
            return fatherId;
        }

        public void setFatherId(String fatherId) {
            this.fatherId = fatherId;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }



        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
