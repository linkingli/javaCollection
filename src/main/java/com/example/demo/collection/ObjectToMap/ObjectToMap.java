package com.example.demo.collection.ObjectToMap;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by lijun on 18-12-9.
 */
public class ObjectToMap {
    public static void main(String[] args) {
        tea tea = new tea();
        tea.setColor("red");
        tea.setHomeland("sada");
        tea.setWeiht(18);
        System.out.println(stringObjectMap(tea));
    }
    private static Map<Object,Object> stringObjectMap(Object o)  {
        ImmutableMap.Builder<Object, Object> builder = ImmutableMap.builder();
        for (Field field:o.getClass().getDeclaredFields())
        {
            field.setAccessible(true);
            try {
                builder.put(field.getName(),field.get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        ImmutableMap<Object, Object> build = builder.build();
        return  build;
    }
}
