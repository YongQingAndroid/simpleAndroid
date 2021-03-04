package com.zyq.http.ResultCover;

import com.alibaba.fastjson.JSON;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Reader;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by dell on 2016/11/4.
 */
public class BaseSoapPullXmlResultCover {
//    @Override
//    public Object just(Response mObject, Type type,Object expandValue) throws Exception {
//       String value=  ProseXml(mObject.body().charStream(),String.valueOf(expandValue));
//        if(value.contains("\"status\":\"0\"")){
//            throw new ArithmeticException(value);
//        }
//        return JSON.parseObject(value,type);
//    }
//    private String ProseXml(Reader reader,String element)throws Exception{
//        XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();
//        XmlPullParser xmlPullParser = pullFactory.newPullParser();
//        xmlPullParser.setInput(reader);
//        int eventType=xmlPullParser.getEventType();
//        while(eventType!=XmlPullParser.END_DOCUMENT){
//            String nodeName=xmlPullParser.getName();
//            switch(eventType){
//                case XmlPullParser.START_TAG:{
//                    if(element.equals(nodeName)) {
//                     return  xmlPullParser.nextText();
//                    }
//                    break;
//                }
//                default:
//                    break;
//            }
//            eventType=xmlPullParser.next();
//        }
//        return "";
//    }
//    public static BaseSoapPullXmlResultCover create(){
//        return new BaseSoapPullXmlResultCover();
//    }

}
