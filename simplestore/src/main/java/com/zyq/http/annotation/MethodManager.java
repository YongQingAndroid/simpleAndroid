package com.zyq.http.annotation;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * This network framework is based on the production of okhttp
 * Network framework free open source, and the final right to interpret the author.
 * The author will go regularly to update the business code, but no obligation to notify the user.
 * My open source community account is fengling136
 * Welcome attention
 * Thanks for your use
 * the power by ZYQ
 */
public class MethodManager {
    private okhttp3.Request.Builder builder;
    private String url;
    private Method method;
    private Object[] arg;
    private String soapxml="";
    private boolean ispost=false,isSoap=false;
    public MethodManager(String url,Method method ,Object[] arg){
        this.url=url;
        this.method=method;
        this.arg=arg;
        builder=new okhttp3.Request.Builder();
        proseMethodAnnotation();
        proseParameterAnnotation();
    }
    private void proseMethodAnnotation() {
        Annotation[] Annotations = method.getAnnotations();
        if (Annotations == null && Annotations.length < 1) {
            return;
        }
        for (Annotation item : Annotations) {
            if (item instanceof LightHttpPost) {
                url+=((LightHttpPost)item).value();
                ispost=true;
            } else if (item instanceof LightHttpGet) {
                url+=((LightHttpGet)item).value();
                builder.get();
            }else if(item instanceof LightHttpStream){

            }else if(item instanceof LightHeard){
                LightHeard mLightHeard=((LightHeard)item);
                builder.addHeader(mLightHeard.key(),mLightHeard.value());

            }else if (item instanceof LightHttpSoapXmlElement) {
                soapxml=((LightHttpSoapXmlElement)item).value();
            }
        }
    }
    private void proseParameterAnnotation(){
        if(url==null){
            throw new ArithmeticException("url is null");
        }
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if(parameterAnnotations==null||parameterAnnotations.length<1){
            return;
        }
        int size=parameterAnnotations.length;
        for(int i=0;i<size;i++){
            Annotation mAnnotation= parameterAnnotations[i][0];
            if(mAnnotation instanceof LightQueryPath){
                String kname=((LightQueryPath)mAnnotation).value();
                String value=String.valueOf(arg[i]);
                url=url.replace(kname,value);
            }else if(mAnnotation instanceof LightQuery){
                String kname=((LightQuery)mAnnotation).value();
                url=appendParam(kname,String.valueOf(arg[i]));
            }else if(mAnnotation instanceof LightQueryMap){
               Object map=arg[i];
                if(map instanceof Map){
                    url=appendParams((Map<String,String>)map);
                }else{
                    throw new ArithmeticException("is not have map value");
                }
            }else if(mAnnotation instanceof LightPostEntity){
                if(!ispost){
                    throw new ArithmeticException("is not have post title");
                }
                String json=new Gson().toJson(arg[i]);
                RequestBody mRequestBody=RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);
                builder.post(mRequestBody);
            }else if(mAnnotation instanceof LightSoapBody){
                if(!ispost){
                    throw new ArithmeticException("is not have post title");
                }
                isSoap=true;
                RequestBody mRequestBody=RequestBody.create(MediaType.parse("text/plain; charset=utf-8"),String.valueOf(arg[i]));
                builder.post(mRequestBody);
            }
        }
    }
    private String appendParam(String key,String value) {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(key, value);
        return builder.build().toString();
    }
    private String appendParams(Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }
    public ParameterizedType getreturnType(){
       return  (ParameterizedType)method.getGenericReturnType();
    }
    public okhttp3.Request getHttpRequest(){
        if(isSoap){
            builder.addHeader("Content-Type","text/xml;charset=UTF-8");
        }
        return builder.url(url).build();
    }

    public String getSoapxml() {
        return soapxml;
    }
}
