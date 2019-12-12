package com.zyq.simplestore.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class TableCatch {
  public static int StoreCatchSize=20;
  public  static Map<String ,String> mapCatch=new TabMap<>();
  public static class TabMap<K,V> extends LinkedHashMap<K,V> {
      @Nullable
      @Override
      public V put(K key, V value) {
          if(size()>StoreCatchSize){
              remove(keySet().iterator().next());
          }
          return super.put(key, value);
      }
      @Nullable
      @Override
      public V get(@Nullable Object key) {
          return super.get(key);
      }
  }
}
