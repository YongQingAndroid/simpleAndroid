package com.zyq.jsimleplepicker.citypicker;

import java.util.ArrayList;
import java.util.List;

/**
 * package kotlinTest:com.qing.lightview.material.CityDataArrayList.class
 * 作者：zyq on 2017/7/31 17:10
 * 邮箱：zyq@posun.com
 */
public class CityDataArrayList extends ArrayList<CityBean> {
    public List<String> toListData(){
        List<String> data=new ArrayList<>();
        for(CityBean bean:this){
            data.add(bean.getName());
        }
        return data;
    }
}
